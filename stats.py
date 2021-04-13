import csv

total = 0
draw = 0
p1wins = 0
p2wins = 0
p1first = 0
p1firstwins = 0
p2first = 0
p2firstwins = 0

with open('./logs/outcomes.txt', newline='') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
    for row in spamreader:
        print(row)
        total = total + 1
        if row[3] == 'GAMEOVER DRAW':
            draw = draw + 1
        else:
            if row[1] == '260835976':
                p1first = p1first + 1
                if row[3] == '0':
                    p1wins = p1wins + 1
                    p1firstwins = p1firstwins + 1
                else:
                    p2wins = p2wins + 1
            else:
                p2first = p2first + 1
                if row[3] == '0':
                    p2wins = p2wins + 1
                    p2firstwins = p2firstwins + 1
                else:
                    p1wins = p1wins + 1

print("MyPlayer won " + str(100 * p1wins/total) +"%, OtherPlayer won " +str(100 * p2wins/total)+"%, and draw " +str(100 * draw/total)+"%")
print("MyPlayer went first " + str(100 * p1first/total) +"% and OtherPlayer went first " + str(100 * p2first/total) + "%")
print("MyPlayer won " + str(100 * p1firstwins/p1wins) + "% by going first, and OtherPlayer won " + str(100 * p2firstwins/p2wins) + "% by going first")
