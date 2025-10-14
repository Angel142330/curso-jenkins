import pytest

def suma(a, b):
    return a + b

def test_suma():
    assert suma(1, 1) == 2
    assert suma(1, 2) == 3
    assert suma(2, 2) == 4
    assert suma(3, 3) == 6
    assert suma(4, 4) == 8
    assert suma(5, 5) == 10
    assert suma(6, 6) == 12
    assert suma(7, 7) == 14
    assert suma(8, 8) == 16
    assert suma(9, 9) == 18
    assert suma(10, 10) == 20

def test_suma_fail():
    assert suma(1, 1) == 3
    assert suma(1, 2) == 5
    assert suma(2, 2) == 6
    assert suma(3, 3) == 9
    assert suma(4, 4) == 12
    assert suma(5, 5) == 15
    assert suma(6, 6) == 18
    assert suma(7, 7) == 21
    assert suma(8, 8) == 24
    assert suma(9, 9) == 27
    assert suma(10, 10) == 30