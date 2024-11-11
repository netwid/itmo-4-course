#include <stdint.h>

#include "stm32f1xx.h"
#include "global.h"

char *Morse[] = {
    ".-",   // a
    "-...", // b
    "-.-.", // c
    "-..",  // d
    ".",    // e
    "..-.", // f
    "--.",  // g
    "....", // h
    "..",   // i
    ".---", // j
    "-.-",  // k
    ".-..", // l
    "--",   // m
    "-.",   // n
    "---",  // o
    ".--.", // p
    "--.-", // q
    ".-.",  // r
    "...",  // s
    "-",    // t
    "..-",  // u
    "...-", // v
    ".--",  // w
    "-..-", // x
    "-.--", // y
    "--.."  // z
};

_Bool irqEnabled;
static uint32_t pmask;

void EnableIRQ()
{
    irqEnabled = 1;
    __enable_irq();
    // HAL_NVIC_EnableIRQ(USART1_IRQn);
}

void DisableIRQ(UART_HandleTypeDef *huart)
{
    irqEnabled = 0;
    __disable_irq();
    HAL_UART_Abort_IT(huart);
    // HAL_NVIC_DisableIRQ(USART1_IRQn);
}