import pytest

def multiplicacion(a, b):
    return a * b

def test_multiplicacion():
    assert multiplicacion(1, 1) == 1
    assert multiplicacion(1, 2) == 2
    assert multiplicacion(2, 2) == 4
    assert multiplicacion(3, 3) == 9
    assert multiplicacion(4, 4) == 16
    assert multiplicacion(5, 5) == 25
    assert multiplicacion(6, 6) == 36
    assert multiplicacion(7, 7) == 49
    assert multiplicacion(8, 8) == 64
    assert multiplicacion(9, 9) == 81
    assert multiplicacion(10, 10) == 100

def test_multiplicacion_fail():
    assert multiplicacion(1, 1) == 3
    assert multiplicacion(1, 2) == 5
    assert multiplicacion(2, 2) == 6
    assert multiplicacion(3, 3) == 9
    assert multiplicacion(4, 4) == 12
    assert multiplicacion(5, 5) == 15
    assert multiplicacion(6, 6) == 18
    assert multiplicacion(7, 7) == 21
    assert multiplicacion(8, 8) == 24
    assert multiplicacion(9, 9) == 27
    assert multiplicacion(10, 10) == 30