import random
import numpy as np
import matplotlib.pyplot as plt

class Perceptorn:
    def __init__(self,LR):
        self.LR = LR #wspolczynnik uczenia
        self.weights = [random.uniform(-0.5,0.5),random.uniform(-0.5,0.5),random.uniform(-0.5,0.5)] #wagi w1 i w2
        self.bias = random.randint(-1,1)

    def uczenie(self,dane,wyniki_and):

        rozmiar = len(dane)


        for i in range(100):
            zestaw = random.randint(0,rozmiar-1)
            y = dane[zestaw][0] * self.weights[0] + dane[zestaw][1] * self.weights[1]+ self.bias #wylicz wyjscie

            if y <= 0:
               aktualny_wyniki = 0 #jesli mniejszy to daj 0
            else:
                aktualny_wyniki = 1 #jesli wiekszy to 1

            if wyniki_and[zestaw] == aktualny_wyniki:
                continue
            else :
                blad = wyniki_and[zestaw] - aktualny_wyniki #nasz blad

            if blad != 0:
                self.bias = self.bias + (blad * self.LR)

            if dane[zestaw][0] == 1:
                self.weights[0] = self.weights[0] +  ( blad * self.LR)
            if dane[zestaw][1] == 1:
                self.weights[1] = self.weights[1] +  (blad * self.LR)

        return self.weights, self.bias

class Siec:

    def __init__(self,LR):
        self.LR = LR
        self.dane = np.array([[0,0],[0,1],[1,0],[1,1]])
        self.Wyniki_OR = np.array([0,1,1,1])
        self.Wyniki_NAND = np.array([1,1,1,0])
        self.Wyniki_AND = np.array([0,0,0,1])

        #OR
        OR = Perceptorn(self.LR)
        self.wspol_OR, self.biasOR =  OR.uczenie(self.dane,self.Wyniki_OR)

        #NAND
        NAND = Perceptorn(self.LR)
        self.wspol_NAND,self.biasNAND = NAND.uczenie(self.dane,self.Wyniki_NAND)

        #AND
        AND  = Perceptorn(self.LR)
        self.wspol_AND , self.biasAND = AND.uczenie(self.dane,self.Wyniki_AND)


    def Oblicz(self, x1, x2, Dane_Wspl , bias):
        y = Dane_Wspl[0] * x1 + x2* Dane_Wspl[1] + bias
        return 1 if y > 0 else 0


    def XOR_WYNIK(self,x1,x2):
        wynik_OR = self.Oblicz(x1,x2,self.wspol_OR,self.biasOR)
        wynik_NAND = self.Oblicz(x1,x2,self.wspol_NAND,self.biasNAND)

        wynik_Kon = self.Oblicz(wynik_OR,wynik_NAND,self.wspol_AND,self.biasAND)
        return wynik_Kon



Moje_AI =  Siec(0.1)
i = Moje_AI.XOR_WYNIK(1,1)
print(i)