#!/bin/bash


source venv/bin/activate

echo "Activando entorno virtual"

pip install -r requirements.txt

echo "Ejecutando pruebas de suma"
pytest test/ --junitxml=reports/test-results.xml --html=reports/test-results.html --self-contained-html

echo "Pruebas finalizadas"