# Jenkins Shared Library 📚

Biblioteca compartida de Jenkins con funciones reutilizables para pipelines en Groovy.

## 📁 Estructura

```
jenkins-shared-library/
├── vars/                      # Funciones globales
│   ├── buildMaven.groovy
│   ├── testMaven.groovy
│   ├── compileMaven.groovy
│   ├── deployApp.groovy
│   ├── dockerBuild.groovy
│   ├── runPytest.groovy
│   ├── notifySlack.groovy
│   └── gitCheckout.groovy
├── src/                       # Clases Groovy
│   └── org/miempresa/
│       └── Utils.groovy
└── resources/                 # Archivos de recursos
```

## 🚀 Configuración en Jenkins

### Opción 1: Global Pipeline Library (Recomendado)

1. Ve a **Jenkins → Administrar Jenkins → Configure System**
2. Busca la sección **Global Pipeline Libraries**
3. Haz clic en **Add**
4. Configura:
   - **Name**: `jenkins-shared-library`
   - **Default version**: `develop` (o `main`)
   - **Load implicitly**: ☑️ (opcional)
   - **Allow default version to be overridden**: ☑️
   - **Modern SCM**: Git
   - **Project Repository**: URL de tu repositorio
   - **Library Path**: `jenkins-shared-library/`

### Opción 2: Uso directo desde repositorio

Si la biblioteca está en el mismo repositorio que tu proyecto:

```groovy
library identifier: 'jenkins-shared-library@develop', 
        retriever: modernSCM([$class: 'GitSCMSource', 
                             remote: 'https://github.com/tu-usuario/curso-jenkins.git'])
```

## 📖 Uso de las Funciones

### 1. buildMaven

Ejecuta `mvn clean install` en un proyecto Maven.

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
    }
}
```

### 2. testMaven

Ejecuta `mvn test` en un proyecto Maven.

```groovy
stage('Test') {
    steps {
        testMaven('mi-proyecto')
    }
}
```

### 3. compileMaven

Compila un proyecto Maven con `mvn compile`.

```groovy
stage('Compile') {
    steps {
        compileMaven('mi-proyecto')
    }
}
```

### 4. deployApp

Despliega una aplicación con confirmación opcional para producción.

```groovy
stage('Deploy') {
    steps {
        deployApp(
            appName: 'mi-aplicacion',
            environment: 'production',
            deployCommand: 'kubectl apply -f k8s/deploy.yaml'
        )
    }
}
```

### 5. dockerBuild

Construye una imagen Docker.

```groovy
stage('Docker Build') {
    steps {
        dockerBuild('mi-app', "${env.BUILD_NUMBER}")
        // o con valores por defecto
        dockerBuild('mi-app')
    }
}
```

### 6. runPytest

Ejecuta tests de Python con pytest.

```groovy
stage('Test Python') {
    steps {
        runPytest('proyecto_pytest', 'venv', 'reports')
    }
}
```

### 7. notifySlack

Envía notificaciones a Slack (requiere configuración previa).

```groovy
post {
    success {
        notifySlack('Build exitoso!', 'SUCCESS')
    }
    failure {
        notifySlack('Build falló', 'FAILURE')
    }
}
```

### 8. gitCheckout

Hace checkout de un repositorio Git.

```groovy
stage('Checkout') {
    steps {
        gitCheckout(
            url: 'https://github.com/usuario/repo.git',
            branch: 'develop',
            credentials: 'github-token'
        )
    }
}
```

### 9. Clase Utils

Utilidades avanzadas desde clases Groovy.

```groovy
@Library('jenkins-shared-library') _
import org.miempresa.Utils

def utils = new Utils(this)

pipeline {
    agent any
    stages {
        stage('Get Version') {
            steps {
                script {
                    def version = utils.getProjectVersion('VERSION')
                    echo "Project version: ${version}"
                    
                    def timestamp = utils.getTimestamp()
                    echo "Build timestamp: ${timestamp}"
                    
                    if (utils.isReleaseBranch(env.BRANCH_NAME)) {
                        echo "Esta es una rama de release"
                    }
                }
            }
        }
    }
}
```

## 📝 Ejemplo Completo

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build Maven') {
            steps {
                buildMaven('mi-proyecto')
            }
        }
        
        stage('Test Maven') {
            steps {
                testMaven('mi-proyecto')
            }
        }
        
        stage('Build Docker') {
            steps {
                dockerBuild('mi-app', "${env.BUILD_NUMBER}")
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                deployApp(
                    appName: 'mi-app',
                    environment: 'production',
                    deployCommand: 'echo "Desplegando..."'
                )
            }
        }
    }
    
    post {
        success {
            notifySlack('Pipeline completado exitosamente!', 'SUCCESS')
        }
        failure {
            notifySlack('Pipeline falló', 'FAILURE')
        }
        always {
            echo 'Pipeline finalizado'
        }
    }
}
```

## 🔧 Personalización

### Agregar nuevas funciones

1. Crea un archivo en `vars/` con el nombre de tu función:
   ```groovy
   // vars/miNuevaFuncion.groovy
   def call(String param) {
       echo "Ejecutando: ${param}"
       // Tu lógica aquí
   }
   ```

2. Úsala directamente en tu pipeline:
   ```groovy
   miNuevaFuncion('mi-parametro')
   ```

### Agregar clases utilitarias

1. Crea una clase en `src/org/miempresa/`:
   ```groovy
   package org.miempresa
   
   class MiClase implements Serializable {
       def steps
       
       MiClase(steps) {
           this.steps = steps
       }
       
       def miMetodo() {
           // Tu lógica
       }
   }
   ```

2. Impórtala en tu pipeline:
   ```groovy
   import org.miempresa.MiClase
   
   def utils = new MiClase(this)
   utils.miMetodo()
   ```

## 📚 Recursos

- [Jenkins Shared Libraries Documentation](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
- [Groovy Documentation](https://groovy-lang.org/documentation.html)
- [Pipeline Steps Reference](https://www.jenkins.io/doc/pipeline/steps/)

## 🤝 Contribuir

1. Crea una nueva función en `vars/`
2. Documenta su uso en este README
3. Haz commit y push
4. ¡Listo para usar!

---

**Nota**: Recuerda actualizar la versión/rama de la biblioteca en tu configuración de Jenkins cuando hagas cambios importantes.

