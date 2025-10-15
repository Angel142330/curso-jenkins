# 🚀 Quick Start - Jenkins Shared Library

Guía rápida de 5 minutos para empezar a usar la Shared Library.

## ⚡ Inicio Rápido (5 minutos)

### Paso 1: Configurar en Jenkins (2 minutos)

1. Abre Jenkins → **Administrar Jenkins** → **Configure System**
2. Busca **Global Pipeline Libraries**
3. Haz clic en **Add** y configura:

```
Name: jenkins-shared-library
Default version: develop
☑️ Load implicitly
☑️ Allow default version to be overridden

Retrieval method: Modern SCM
  └─ Git
     └─ Project Repository: https://github.com/Angel142330/curso-jenkins.git
     └─ Library Path: jenkins-shared-library/
```

4. Guardar

### Paso 2: Crear tu Pipeline (2 minutos)

Crea un nuevo Pipeline en Jenkins y usa este Jenkinsfile:

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
        stage('Test') {
            steps {
                testMaven('mi-proyecto')
            }
        }
    }
    post {
        success {
            notifySlack('Build exitoso!', 'SUCCESS')
        }
    }
}
```

### Paso 3: Ejecutar (1 minuto)

1. Guarda el pipeline
2. Haz clic en **Build Now**
3. ¡Listo! 🎉

---

## 📖 Funciones Disponibles

| Función | Descripción | Ejemplo |
|---------|-------------|---------|
| `buildMaven()` | Ejecuta `mvn clean install` | `buildMaven('mi-proyecto')` |
| `testMaven()` | Ejecuta `mvn test` | `testMaven('mi-proyecto')` |
| `compileMaven()` | Ejecuta `mvn compile` | `compileMaven('mi-proyecto')` |
| `dockerBuild()` | Construye imagen Docker | `dockerBuild('app', '1.0')` |
| `deployApp()` | Despliega aplicación | `deployApp(appName: 'app', environment: 'prod')` |
| `runPytest()` | Ejecuta tests Python | `runPytest('proyecto_pytest')` |
| `notifySlack()` | Envía notificación | `notifySlack('Mensaje', 'SUCCESS')` |
| `gitCheckout()` | Checkout de repo | `gitCheckout(url: '...', branch: 'main')` |

---

## 🎯 Casos de Uso Comunes

### Maven + Deploy

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build') { steps { buildMaven('mi-proyecto') } }
        stage('Test') { steps { testMaven('mi-proyecto') } }
        stage('Deploy') { 
            steps { 
                deployApp(
                    appName: 'mi-app', 
                    environment: 'production',
                    deployCommand: 'kubectl apply -f k8s/'
                ) 
            } 
        }
    }
}
```

### Docker Build + Push

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build') { 
            steps { 
                dockerBuild('mi-app', "${env.BUILD_NUMBER}") 
                sh 'docker push mi-app:${BUILD_NUMBER}'
            } 
        }
    }
}
```

### Python Tests

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Test') { 
            steps { 
                runPytest('proyecto_pytest', 'venv', 'reports') 
            } 
        }
    }
}
```

---

## 🔗 Más Información

- **Todas las funciones**: [README.md](README.md)
- **Configuración detallada**: [GUIA_CONFIGURACION.md](GUIA_CONFIGURACION.md)
- **Más ejemplos**: [EJEMPLOS.md](EJEMPLOS.md)

---

## 🆘 Problemas Comunes

### ❌ "Library not found"
**Solución**: Verifica que el nombre en `@Library('jenkins-shared-library')` coincida con el configurado en Jenkins.

### ❌ "No such DSL method"
**Solución**: La función no existe. Verifica la lista de funciones disponibles arriba.

### ❌ "Script not yet approved"
**Solución**: Ve a **Administrar Jenkins** → **In-process Script Approval** y aprueba.

---

¡Eso es todo! En 5 minutos tienes tu Shared Library funcionando 🚀

