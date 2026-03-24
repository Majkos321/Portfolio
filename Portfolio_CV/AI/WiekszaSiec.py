import random
import numpy as np

class perceptron:

    def __init__(self,LR):
        self.weight = np.array([
            [random.uniform(-0.5,0.5),random.uniform(-0.5,0.5),random.uniform(-0.5,0.5)],#wagi dla p1 (x1,x2,tryb)
            [random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5)],#wagi dla p2
            [random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5)],#wagi dla p3
            [random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5)],#wagi dla p4
        ]) #macierz wag dla naszej sieci

        self.weight_secound = np.array([random.uniform(-0.5,0.5),random.uniform(-0.5,0.5),random.uniform(-0.5,0.5),random.uniform(-0.5,0.5)])
        self.bias_secound = random.uniform(-0.5,0.5)
        self.LR = LR
        # Zmień tę linię:
        self.bias = np.array([random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5), random.uniform(-0.5, 0.5),
                              random.uniform(-0.5, 0.5)])

    def uczenie(self):

        self.XOR_DANE = np.array([
            [0, 0, 2],
            [0, 1, 2],
            [1, 0, 2],
            [1, 1, 2]])
        self.XOR_WYN = np.array([0, 1, 1, 0])

        self.OR_DANE = np.array([
            [0,0,0],
            [0,1,0],
            [1,0,0],
            [1,1,0]
        ])
        self.OR_WYN = np.array([0,1,1,1])

        self.AND_DANE = np.array([
            [0,0,1],
            [0,1,1],
            [1,0,1],
            [1,1,1]
        ])
        self.AND_WYN = np.array([0,0,0,1])

        self.zestawuczenia = [
            [self.OR_DANE, self.OR_WYN],#0 gdy 0 to uczymy OR  nastepnie zawsze przy waga [0] bo to wejscia a nasptenie wyubieramy zestaw
            [self.AND_DANE, self.AND_WYN], #1 gdy 1 to uczymy AND
            [self.XOR_DANE, self.XOR_WYN]]

        bladwag = 1

        while(bladwag > 0.001):
            AND_CZY_OR = random.randint(0,2)
            zestaw = random.randint(0,3) #dosc skomplikowanie ale no po porstu mamy lsicie w lisice ktora ma maceirz i wektor
            p1 = (self.weight[0][0] * self.zestawuczenia[AND_CZY_OR][0][zestaw][0] + self.weight[0][1] * self.zestawuczenia[AND_CZY_OR][0][zestaw][1]
                 + self.weight[0][2] * self.zestawuczenia[AND_CZY_OR][0][zestaw][2]) + self.bias[0] # x1*w1,x2*w2 tryb* w3
            p2 = (self.weight[1][0] * self.zestawuczenia[AND_CZY_OR][0][zestaw][0] + self.weight[1][1] *
                  self.zestawuczenia[AND_CZY_OR][0][zestaw][1]
                  + self.weight[1][2] * self.zestawuczenia[AND_CZY_OR][0][zestaw][2]) + self.bias[1]
            p3 = (self.weight[2][0] * self.zestawuczenia[AND_CZY_OR][0][zestaw][0] + self.weight[2][1] *
                  self.zestawuczenia[AND_CZY_OR][0][zestaw][1]
                  + self.weight[2][2] * self.zestawuczenia[AND_CZY_OR][0][zestaw][2]) + self.bias[2]
            p4 = (self.weight[3][0] * self.zestawuczenia[AND_CZY_OR][0][zestaw][0] + self.weight[3][1] *
                  self.zestawuczenia[AND_CZY_OR][0][zestaw][1]
                  + self.weight[3][2] * self.zestawuczenia[AND_CZY_OR][0][zestaw][2]) + self.bias[3]

            p1 = 1/ (1 + np.exp(-p1))
            p2 = 1 / (1 + np.exp(-p2))
            p3 = 1 / (1 + np.exp(-p3))
            p4 = 1 / (1 + np.exp(-p4))

            p5 = self.weight_secound[0] * p1 + self.weight_secound[1] * p2 + self.weight_secound[2] * p3 + self.weight_secound[3] * p4 + self.bias_secound

            p5 = 1 / (1 + np.exp(-p5))

            blad = self.zestawuczenia[AND_CZY_OR][1][zestaw] - p5
            delta_p5 = blad * (p5 * (1-p5)) #pochodna sigmoidy

            self.weight_secound[0] += self.LR * delta_p5 * p1
            self.weight_secound[1] += self.LR * delta_p5 * p2
            self.weight_secound[2] += self.LR * delta_p5 * p3
            self.weight_secound[3] += self.LR * delta_p5 * p4
            self.bias_secound += self.LR * delta_p5


            wejscia = self.zestawuczenia[AND_CZY_OR][0][zestaw]
            p_outputs = [p1, p2, p3, p4]

            for i in range(4):

                delta_h = delta_p5 * self.weight_secound[i] * (p_outputs[i] * (1 - p_outputs[i]))

                for j in range(3):
                    self.weight[i][j] += self.LR * delta_h * wejscia[j]

                self.bias[i] += self.LR * delta_h

            bladwag = abs(blad)

    def przewiduj(self, x1, x2, tryb):
        h = []
        for i in range(4):
            suma = (self.weight[i][0] * x1 +
                    self.weight[i][1] * x2 +
                    self.weight[i][2] * tryb) + self.bias[i]
            h.append(1 / (1 + np.exp(-suma)))

        suma_final = (h[0] * self.weight_secound[0] +
                      h[1] * self.weight_secound[1] +
                      h[2] * self.weight_secound[2] +
                      h[3] * self.weight_secound[3]) + self.bias_secound
        y = 1 / (1 + np.exp(-suma_final))

        return 1 if y >= 0.5 else 0

MojeAI = perceptron(0.1)
tab = MojeAI.uczenie()

print(MojeAI.przewiduj(1,1,2 ))
print(MojeAI.przewiduj(1,1,1 ))
print(MojeAI.przewiduj(1,1,0))
