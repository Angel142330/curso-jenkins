#!/bin/bash

if [! -d "venv" ]; then
    echo "Creando entorno virtual"
    python3 -m venv venv
fi

#Activar el entorno virtual correctamente
if [ -f "venv/bin/activate" ]; then
    source venv/bin/activate
elif [ -f "venv/Scripts/activate" ]; then
    source venv/Scripts/activate
fi

source venv/bin/activate
echo "Activando entorno virtual"

pip install -r requirements.txt

echo "Ejecutando pruebas de suma"
pytest test/ --junitxml=reports/test-results.xml --html=reports/test-results.html --self-contained-html

echo "Pruebas finalizadas"