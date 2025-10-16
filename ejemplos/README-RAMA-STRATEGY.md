# 🌿 Pipeline con Estrategia por Rama

Esta guía explica cómo implementar una pipeline de Jenkins que ejecuta diferentes pasos según la rama Git.

## 📋 Estrategia por Rama

### 🌱 **Rama `feature/*`**

```
✅ Kiuwan Validator
   └─→ Analiza el código
   └─→ BORRA los resultados después (no permanecen)
   └─→ FIN (no más pasos)
```

**Objetivo**: Validación rápida de código en ramas de desarrollo de features.

---

### 🟢 **Rama `develop`**

```
✅ Kiuwan Validator
   └─→ Analiza el código

📋 Revisar pom.xml
   └─→ Lee property: <develop.runTests>

❓ ¿develop.runTests = true?
   ├─ SÍ (true):
   │  ├─ ⚙️ Compilar
   │  ├─ 🧪 Ejecutar tests unitarios
   │  └─ 💾 Insertar resultados en BD
   │
   └─ NO (false):
      └─ ⚙️ Compilar (sin tests)
```

**Configuración en `pom.xml`**:

```xml
<properties>
  <!-- Cambiar a false para saltarse tests en develop -->
  <develop.runTests>true</develop.runTests>
</properties>
```

---

### 🔴 **Rama `master` / `main`**

```
✅ Kiuwan Validator
   └─→ Analiza el código

⚙️ Compilar
   └─→ mvn clean install

🧪 Tests Unitarios (OBLIGATORIOS)
   └─→ Maneja tests excepcionados

🔄 Tests de Regresión (OBLIGATORIOS)
   └─→ Maneja tests excepcionados

🔗 Tests de Integración (OBLIGATORIOS)
   └─→ Verifica todo el sistema

💾 Insertar resultados en BD
```

**Características**:
- **Todas las pruebas son obligatorias**
- Tests excepcionados se marcan como OK pero no fallan el build
- Resultados completos se guardan en base de datos

---

## 🔧 Archivos Creados

### 1. **Funciones Globales (vars/)**

| Archivo | Descripción |
|---------|-------------|
| `kiuwanValidator.groovy` | Ejecuta análisis Kiuwan, borra resultados si es feature/* |
| `checkPomConfig.groovy` | Lee `<develop.runTests>` del pom.xml |
| `runRegressionTests.groovy` | Ejecuta tests de regresión con manejo de excepciones |
| `insertTestResultsToDb.groovy` | Inserta resultados en base de datos |

### 2. **Jenkinsfile**

`ejemplos/Jenkinsfile-rama-strategy` - Pipeline completa con toda la lógica

### 3. **Configuración**

`mi-proyecto/pom.xml` - Incluye property `<develop.runTests>`

---

## 🚀 Uso

### Opción 1: Copiar el Jenkinsfile a tu proyecto

```bash
cp ejemplos/Jenkinsfile-rama-strategy mi-proyecto/Jenkinsfile
```

### Opción 2: Crear pipeline Multibranch en Jenkins

1. Jenkins → New Item → **Multibranch Pipeline**
2. Configurar repositorio Git
3. Branch Sources:
   - `feature/*`
   - `develop`
   - `master`
4. Script Path: `mi-proyecto/Jenkinsfile`

---

## ⚙️ Configuración de Tests Excepcionados

En el Jenkinsfile, configura los tests que están permitidos fallar:

```groovy
environment {
    // Tests que si fallan, se marcan como OK (no fallan el build)
    EXCEPTED_TESTS = 'TestLogin,TestPayment,TestOldFeature'
}
```

---

## 💾 Configuración de Base de Datos

Para insertar resultados en BD, necesitas:

### 1. Crear tabla en PostgreSQL

```sql
CREATE TABLE test_results (
    id SERIAL PRIMARY KEY,
    project_name VARCHAR(100),
    branch VARCHAR(50),
    test_result VARCHAR(10),  -- 'PASS' o 'FAIL'
    build_number INTEGER,
    executed_at TIMESTAMP DEFAULT NOW()
);
```

### 2. Configurar credenciales en Jenkins

1. Jenkins → Credentials → Add
2. Tipo: Username with password
3. ID: `db-user` / `db-password`

### 3. Actualizar la URL en `insertTestResultsToDb.groovy`

```groovy
url: 'jdbc:postgresql://TU_HOST:5432/jenkins_db',
```

---

## 📊 Ejemplo de Ejecución

### Feature Branch: `feature/nueva-funcionalidad`

```
[Pipeline] stage 'Kiuwan Validator'
🔍 Validando con Kiuwan (rama: feature/nueva-funcionalidad)
✅ Análisis Kiuwan completado
🗑️ Borrando resultados de Kiuwan (rama feature/*)

[Pipeline] stage 'Feature Branch Strategy'
🌿 Estrategia FEATURE: Solo Kiuwan
✅ Feature validada - No más pasos necesarios

SUCCESS ✅
```

### Develop Branch con `<develop.runTests>true</develop.runTests>`

```
[Pipeline] stage 'Kiuwan Validator'
✅ Análisis Kiuwan completado

[Pipeline] stage 'Develop - Check POM Config'
📋 Configuración desde pom.xml:
   → develop.runTests = true
   → Insertar en BD = true

[Pipeline] stage 'Develop - Compile'
⚙️ Compilando...

[Pipeline] stage 'Develop - Tests (condicional)'
🧪 POM indica RUN_TESTS=true → Ejecutando tests
✅ Tests completados
💾 Insertando resultados en la base de datos...

SUCCESS ✅
```

### Develop Branch con `<develop.runTests>false</develop.runTests>`

```
[Pipeline] stage 'Kiuwan Validator'
✅ Análisis Kiuwan completado

[Pipeline] stage 'Develop - Check POM Config'
📋 Configuración desde pom.xml:
   → develop.runTests = false
   → Insertar en BD = false

[Pipeline] stage 'Develop - Compile'
⚙️ Compilando...

[Pipeline] stage 'Develop - Skip Tests'
⏭️ POM indica RUN_TESTS=false → Saltando tests

SUCCESS ✅
```

### Master Branch

```
[Pipeline] stage 'Kiuwan Validator'
✅ Análisis Kiuwan completado

[Pipeline] stage 'Master - Compile'
🌿 Estrategia MASTER: Todas las pruebas obligatorias
⚙️ Build completado

[Pipeline] stage 'Master - Unit Tests'
🧪 Ejecutando pruebas unitarias (obligatorias)...
⚠️ Test 'TestLogin' EXCEPCIONADO → Continuando como OK
✅ Tests completados
💾 Resultados insertados en BD

[Pipeline] stage 'Master - Regression Tests'
🔄 Ejecutando pruebas de regresión (obligatorias)...
✅ Regresión completada

[Pipeline] stage 'Master - Integration Tests'
🔗 Ejecutando pruebas de integración (obligatorias)...
✅ Integración completada

[Pipeline] stage 'Master - All Tests Summary'
═══════════════════════════════════════
📊 RESUMEN DE PRUEBAS (MASTER)
═══════════════════════════════════════
✅ Kiuwan: PASS
✅ Unit Tests: PASS
✅ Regression Tests: PASS
✅ Integration Tests: PASS
═══════════════════════════════════════

SUCCESS ✅
```

---

## 🎯 Personalización

### Cambiar comportamiento de develop

Edita `mi-proyecto/pom.xml`:

```xml
<!-- Para ejecutar tests en develop -->
<develop.runTests>true</develop.runTests>

<!-- Para NO ejecutar tests en develop -->
<develop.runTests>false</develop.runTests>
```

### Agregar más tests excepcionados

Edita el Jenkinsfile:

```groovy
EXCEPTED_TESTS = 'Test1,Test2,Test3,TestQueNoQueremosQueFalle'
```

### Cambiar comando de Kiuwan

Edita `vars/kiuwanValidator.groovy`:

```groovy
sh '''
    # Reemplaza con tu comando real de Kiuwan
    kiuwan-local-analyzer.sh -n "mi-proyecto" -s . -l /path/to/kiuwan/
'''
```

---

## 🆘 Troubleshooting

### "develop.runTests not found"
**Solución**: Asegúrate de que el pom.xml tiene la property en `<properties>`

### "No database connection"
**Solución**: Verifica las credenciales en Jenkins y que la BD esté accesible

### "Test excepcionado no funciona"
**Solución**: Revisa que el nombre del test en `EXCEPTED_TESTS` coincida exactamente con el nombre real

---

## ✅ Checklist de Implementación

- [ ] Copiar funciones a `vars/`
- [ ] Agregar property `<develop.runTests>` al pom.xml
- [ ] Configurar tabla en base de datos
- [ ] Agregar credenciales en Jenkins
- [ ] Crear pipeline Multibranch en Jenkins
- [ ] Configurar lista de tests excepcionados
- [ ] Actualizar comando de Kiuwan si es necesario
- [ ] Probar en rama `feature/*`
- [ ] Probar en rama `develop` con true/false
- [ ] Probar en rama `master`

---

**¡Listo para "ventilárselo"!** 😎🚀

