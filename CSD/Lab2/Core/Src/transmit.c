#include <stdlib.h>
#include <string.h>

#include "main.h"
#include "transmit.h"
#include "global.h"

static uint32_t buttonPressTime = 0;

typedef enum
{
    BUTTON_NO_PRESS,
    BUTTON_SHORT_PRESS,
    BUTTON_LONG_PRESS
} ButtonPressType;

ButtonPressType GetButtonState(void)
{
    GPIO_PinState currentState = HAL_GPIO_ReadPin(BUTTON_GPIO_PORT, BUTTON_PIN);
    uint32_t currentTime = HAL_GetTick();

    if (currentState == GPIO_PIN_RESET && buttonPressTime == 0)
    {
        buttonPressTime = currentTime;
        return BUTTON_NO_PRESS;
    }

    uint32_t diffTime = currentTime - buttonPressTime;
    if (currentState == GPIO_PIN_SET)
    {
        if (diffTime > DEBOUNCE_DELAY && buttonPressTime > 0)
        {
            buttonPressTime = 0;
            if (diffTime > LONG_PRESS_DELAY)
            {
                return BUTTON_LONG_PRESS;
            }
            return BUTTON_SHORT_PRESS;
        }
        else
        {
            buttonPressTime = 0;
        }
    }

    return BUTTON_NO_PRESS;
}

void SendUART(UART_HandleTypeDef *huart, char *morseCode)
{
    for (int i = 0; i < sizeof(Morse) / sizeof(Morse[0]); i++)
    {
        if (strcmp(morseCode, Morse[i]) == 0)
        {
            char letter = 'a' + i;
            if (irqEnabled) {
                HAL_UART_Transmit_IT(huart, (uint8_t *)&letter, 1);
            } else {
                HAL_UART_Transmit(huart, (uint8_t *)&letter, 1, HAL_MAX_DELAY);
            }
            return;
        }
    }

    if (irqEnabled) {
        HAL_UART_Transmit_IT(huart, (uint8_t *)"?", 1);
    } else {
        HAL_UART_Transmit(huart, (uint8_t *)"?", 1, HAL_MAX_DELAY);
    }
}

static char morseBuffer[8];
static uint8_t morseIndex = 0;
static uint32_t lastButtonActionTime = 0;

void HandleTransmit(UART_HandleTypeDef *huart)
{
    ButtonPressType pressType = GetButtonState();
    uint32_t currentTime = HAL_GetTick();

    if (pressType == BUTTON_SHORT_PRESS)
    {
        if (morseIndex < sizeof(morseBuffer) - 1)
        {
            morseBuffer[morseIndex++] = '.';
            morseBuffer[morseIndex] = '\0';
            lastButtonActionTime = currentTime;
        }
    }
    else if (pressType == BUTTON_LONG_PRESS)
    {
        if (morseIndex < sizeof(morseBuffer) - 1)
        {
            morseBuffer[morseIndex++] = '-';
            morseBuffer[morseIndex] = '\0';
            lastButtonActionTime = currentTime;
        }
    }

    if ((currentTime - lastButtonActionTime) > IDLE_TIMEOUT && morseIndex > 0)
    {
        SendUART(huart, (char *)morseBuffer);
        morseIndex = 0;
    }
}