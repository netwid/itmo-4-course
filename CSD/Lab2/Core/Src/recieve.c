#include <stdlib.h>

#include "main.h"
#include "recieve.h"
#include "queue.h"
#include "global.h"

void TurnLedOn()
{
  HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_SET);
}
void TurnLedOff()
{
  HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_RESET);
}

static char currentChar = 0;
static size_t index = 0;
static uint32_t lastActionTime = 0;
static _Bool isLedOn = 0;
static _Bool isIdle = 1;
static _Bool symbolFinished = 0;

void HandleReceivedMorse()
{
  uint32_t currentTime = HAL_GetTick();

  if (symbolFinished)
  {
    if (currentTime - lastActionTime > INTER_SYMBOL_TIMEOUT)
    {
      isIdle = 1;
      symbolFinished = 0;
    }
    else
    {
      return;
    }
  }

  if (isIdle)
  {
    currentChar = QueuePop();
    index = 0;
    if (currentChar == 0)
    {
      TurnLedOff();
      return;
    }
    isIdle = 0;
  }

  char currentSymbol = Morse[currentChar - 'a'][index];

  if (isLedOn)
  {
    if ((currentSymbol == '.' && currentTime - lastActionTime >= DOT_DURATION) ||
        (currentSymbol == '-' && currentTime - lastActionTime >= DASH_DURATION))
    {
      TurnLedOff();
      isLedOn = 0;
      index++;
      lastActionTime = currentTime;
    }
  }
  else
  {
    if (currentSymbol == '\0')
    {
      TurnLedOff();
      symbolFinished = 1;
      index = 0;
      return;
    }
    else if (currentTime - lastActionTime >= INTER_ELEMENT_TIMEOUT)
    {
      // Моргнуть светодиодом в зависимости от символа
      TurnLedOn();
      isLedOn = 1;
      lastActionTime = currentTime;
    }
  }
}

static uint8_t receivedChar;

void HAL_UART_RxCpltCallback(UART_HandleTypeDef *huart)
{
  if (receivedChar == '+')
  {
    irqEnabled = 0;
    return;
  }

  QueuePush(receivedChar);
  HAL_UART_Receive_IT(huart, &receivedChar, 1);
}

void HandleReceive(UART_HandleTypeDef *huart)
{
  // Recieve UART ans push to queue
  if (!irqEnabled && HAL_UART_Receive(huart, &receivedChar, 1, 10) == HAL_OK)
  {
    if (receivedChar == '+')
    {
      irqEnabled = 1;
      HAL_UART_Receive_IT(huart, &receivedChar, 1);
      return;
    }
    QueuePush(receivedChar);
  }

  HandleReceivedMorse();
}