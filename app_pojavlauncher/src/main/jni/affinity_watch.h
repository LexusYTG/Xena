#include <dirent.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/syscall.h>

static void aw_apply(int tid, const char *name) {
    unsigned long mask;
    if (strncmp(name, "JNISurfaceTextu", 15) == 0) mask = 0x80UL;
    else if (strstr(name, "CompilerThre") != NULL) mask = 0x3fUL;
    else mask = 0x7fUL;
    syscall(__NR_sched_setaffinity, tid, sizeof(mask), &mask);
}

static void *aw_loop(void *arg) {
    (void)arg;
    for (;;) {
        DIR *d = opendir("/proc/self/task");
        if (d) {
            struct dirent *e;
            while ((e = readdir(d)) != NULL) {
                if (e->d_name[0] == '.') continue;
                int tid = atoi(e->d_name);
                char path[64];
                char name[32] = {0};
                snprintf(path, sizeof(path), "/proc/self/task/%d/comm", tid);
                FILE *f = fopen(path, "r");
                if (!f) continue;
                if (fgets(name, sizeof(name), f)) {
                    name[strcspn(name, "\n")] = 0;
                    aw_apply(tid, name);
                }
                fclose(f);
            }
            closedir(d);
        }
        sleep(1);
    }
    return NULL;
}

static void affinity_watch_start(void) {
    pthread_t t;
    if (pthread_create(&t, NULL, aw_loop, NULL) == 0) pthread_detach(t);
}
