def prost(n):
    for i in range(2, n - 1):
        if n % i == 0:
            print(f'Число {n} делится на', i)
            return 'Нет'
    return 'Да'


print('Введите число для проверки на простоту (0 для выхода): ', end='')
n = int(input())
while n != 0:
    x = prost(n)
    if x == 'Нет':
        print("Число простое?", x)
    else:
        print("Число простое?", x)
    print('Введите число для проверки на простоту (0 для выхода): ', end='')
    n = int(input())
