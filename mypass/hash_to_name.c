//
//  main.c
//  asdfasdf
//
//  Created by 방정호 on 12/7/17.
//  Copyright © 2017 방정호. All rights reserved.
//

#include <stdio.h>

//hashes c_string to int, makes it easy to pass loop_id to external function
unsigned long hash(char *str)
{
    unsigned long hash = 5381;
    int c;
    while ((c = *str++))
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
    return hash%8971;;
}

int main(int argc, const char * argv[]) {
    const char* name = argv[1];
    unsigned long h = hash(name);
    printf("%s\t%lu\n",name, h);
    return 0;
}
