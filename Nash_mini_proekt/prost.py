def prost(n):
    for i in range(2, n - 1):
        if n % i == 0:
            return False
    return True


n = int(input())
print("Число простое?", prost(n))