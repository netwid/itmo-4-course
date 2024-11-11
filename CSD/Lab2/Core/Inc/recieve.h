#ifndef __RECEIVE_H
#define __RECEIVE_H

#define DOT_DURATION 200
#define DASH_DURATION 1000
#define INTER_ELEMENT_TIMEOUT DOT_DURATION
#define INTER_SYMBOL_TIMEOUT (3 * DOT_DURATION)

void HandleReceive(UART_HandleTypeDef *huart);

#endif /* __RECEIVE_H */