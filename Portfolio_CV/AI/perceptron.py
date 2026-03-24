import random
import numpy as np
import matplotlib.pyplot as plt

class Perceptorn:
    def __init__(self,LR):
        self.LR = LR #wspolczynnik uczenia
        self.weights = [random.uniform(-0.5,0.5),random.uniform(-0.5,0.5), random.unfirom(-0.5,0.5),random.unfirom(-0.5,0.5)] #wagi w1 i w2
        self.bias = random.randint(-1,1)

    def uczenie(self,dane,wyniki_and,oczekiwane):

        rozmiar = len(dane)


        for i in range(100):#and
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


def narysuj_granice(wagi,bias,dane_X,dane_Y,tytul):

    x_min ,x_max= -0.5 , 1.5
    y_min, y_max = -0.5, 1.5

    xx, yy = np.meshgrid(np.arange(x_min, x_max, 0.01),
                         np.arange(y_min, y_max, 0.01))

    z = xx * wagi[0] + yy * wagi[1] + bias
    z = np.where(z > 0, 1, 0)
    plt.figure(figsize=(6, 5))
    plt.contourf(xx, yy, z, cmap=plt.cm.RdBu, alpha=0.3)
    plt.scatter(dane_X[:, 0], dane_X[:, 1], c=dane_Y, cmap=plt.cm.RdBu, edgecolors='k', s=100)

    plt.title(tytul)
    plt.xlabel("Wejście x1")
    plt.ylabel("Wejście x2")
    plt.grid(True, linestyle='--')
    plt.show()

dane_and = np.array([
    [1,1],
    [1,0],
    [0,1],
    [0,0]
])

wyniki_or = [1,1,1,0]

wyniki_and = [1,0,0,0]
Pierwszy = Perceptorn(0.1)
wagi_and , bias = Pierwszy.uczenie(dane_and,wyniki_and)

Drugi = Perceptorn(0.1)
wagi_or , biaz = Drugi.uczenie(dane_and, wyniki_or)

print(wagi_and)
print(bias)


narysuj_granice(wagi_and,bias,dane_and,wyniki_and,"Perceptor - bramka AND")





