#ifndef __GLOBAL_H
#define __GLOBAL_H

extern char *Morse[26];
extern _Bool irqEnabled;

void EnableIRQ();
void DisableIRQ(UART_HandleTypeDef *huart);

#endif /* __GLOBAL_H */