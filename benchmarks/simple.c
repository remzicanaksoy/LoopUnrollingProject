#include <stdio.h>
int doStuff ();
int main()
{

  int in[1000]; 
  int i,j;
  FILE* myfile;

  for (i = 0; i < 2; i++)
  {
    for (j = 0; j < 5; j++){
      in[i] = 0;
      int k = doStuff();
    }
  }  
  
  
  for (j = 100; j < 1000; j++)
  {
   for (i = 100; i < 1000; i++){
     in[j] = in[j-9] + 10;
     in[j] += in[j];
   }
  }
  
  
  for (i = 0; i< 1000; i++){
    fprintf(stdout,"%d\n", in[i]);
  }
  
  int k = 0;
  fprintf(stdout,"%d\n", k);
  
  return 1;
}


int doStuff (){
  int j = 0;
  for (int i = 0; i < 5; i++){
    j = j+i;
  }
  return j;
}
