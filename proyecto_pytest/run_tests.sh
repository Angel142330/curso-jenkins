#!/bin/bash

# Eliminar venv existente si no es compatible (por ejemplo, creado en Windows)
if [ -d "venv" ] && [ ! -f "venv/bin/activate" ]; then
    echo "Eliminando entorno virtual incompatible"
    rm -rf venv
fi

# Crear entorno virtual si no existe
if [ ! -d "venv" ]; then
    echo "Creando entorno virtual"
    python3 -m venv venv
fi

# Activar el entorno virtual correctamente
echo "Activando entorno virtual"
if [ -f "venv/bin/activate" ]; then
    source venv/bin/activate
elif [ -f "venv/Scripts/activate" ]; then
    source venv/Scripts/activate
else
    echo "Error: No se pudo encontrar el script de activación del entorno virtual"
    exit 1
fi

# Verificar que el entorno virtual está activado
echo "Python utilizado: $(which python3)"
echo "Pip utilizado: $(which pip)"

# Crear directorio de reportes si no existe
mkdir -p reports

# Instalar dependencias
echo "Instalando dependencias"
pip install -r requirements.txt

# Ejecutar pruebas
echo "Ejecutando pruebas de suma"
venv/bin/python -m pytest test/ --junitxml=reports/test-results.xml --html=reports/test-results.html --self-contained-html

echo "Pruebas finalizadas"