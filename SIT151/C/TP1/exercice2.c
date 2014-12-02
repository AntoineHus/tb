#include <stdio.h>
#include <ctype.h>
#include <string.h>

int lirePhrase(char phrase[]) {
  char delimiter = '"';
  char lastChar;
  int  charCount = 0;
  
  // Move after the first delimiter
  while (getchar() != delimiter);

  while ((lastChar = getchar()) != delimiter) {
    phrase[charCount] = lastChar;
    charCount++;
  }

  // Clear the buffer
  while (getchar() != '\n');

  phrase[charCount] = '\0';
  return charCount;
}

int lireCle() {
  int key;

  do {
    printf("Veuillez entrer une cle (0-255):\n");
    scanf("%d", &key);
  } while ((key < 0) || (key > 255));

  return key;
}

void crypte(char texte[], int longueur, char chaineCryptage[]) {
  int m = strlen(chaineCryptage);

  for (int i = 0; i < longueur; i++) {
    texte[i] = texte[i] ^ chaineCryptage[i % m];
  }
}

void decrypte(char texte[], int longueur, char chaineCryptage[]) {
  int m = strlen(chaineCryptage);

  for (int i = 0; i < longueur; i++) {
    texte[i] = texte[i] ^ chaineCryptage[i % m];
  }
}

void affichePhrase(char phrase[], int longueur) {
  for (int i =  0; i < longueur; i++) {
    if (isprint(phrase[i])) {
      putchar(phrase[i]);
    }
  }
}

int main() {
  printf("Cle de chiffrage: \n");
  char key[255];
  scanf("%s", key);

  printf("Phrase a chiffrer (entre \"):\n");
  char phrase[255];
  int count = lirePhrase(phrase);

  crypte(phrase, count, key);
  affichePhrase(phrase, count);
  printf("\n");

  decrypte(phrase, count, key);
  affichePhrase(phrase, count);
  printf("\n");

  return 0;
}