#ifndef __TRASMIT_H
#define __TRASMIT_H

#define DEBOUNCE_DELAY 20
#define LONG_PRESS_DELAY 600
#define BUTTON_GPIO_PORT GPIOC
#define BUTTON_PIN GPIO_PIN_15
#define IDLE_TIMEOUT 1500

void HandleTransmit(UART_HandleTypeDef *huart);

#endif /* __TRASMIT_H */