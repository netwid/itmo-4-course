#include <stdlib.h>
#include "queue.h"

static char buffer[QUEUE_SIZE];
static size_t start = 0;
static size_t end = 0;

void QueuePush(char c) {
    size_t nextEnd = (end + 1) % QUEUE_SIZE;
    if (nextEnd == start) {
        return;
    }
    buffer[end] = c;
    end = nextEnd;
}

char QueuePop() {
    if (start == end) {
        return 0;
    }
    char ret = buffer[start];
    start = (start + 1) % QUEUE_SIZE;
    return ret;
}
