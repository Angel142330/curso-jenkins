# 🔧 Guía de Configuración - Jenkins Shared Library

Esta guía te muestra paso a paso cómo configurar y usar la Shared Library en Jenkins.

## 📋 Prerequisitos

- Jenkins instalado y funcionando
- Acceso de administrador a Jenkins
- Repositorio Git con el código de la shared library

## 🚀 Configuración Paso a Paso

### Opción 1: Configuración Global (Recomendado para producción)

Esta opción permite que la biblioteca esté disponible para todos los pipelines.

#### 1. Subir la biblioteca al repositorio

```bash
cd curso-jenkins
git add jenkins-shared-library/
git commit -m "Add Jenkins Shared Library"
git push origin develop
```

#### 2. Configurar en Jenkins

1. Abre Jenkins en tu navegador
2. Ve a **Administrar Jenkins** (Manage Jenkins) → **Configure System**
3. Busca la sección **Global Pipeline Libraries**
4. Haz clic en **Add** (Agregar)
5. Configura los siguientes campos:

   | Campo | Valor |
   |-------|-------|
   | **Name** | `jenkins-shared-library` |
   | **Default version** | `develop` |
   | **Load implicitly** | ☑️ (Marcar si quieres que se cargue automáticamente) |
   | **Allow default version to be overridden** | ☑️ |
   | **Include @Library changes in job recent changes** | ☑️ (opcional) |

6. En **Retrieval method**, selecciona **Modern SCM**
7. Selecciona **Git** como Source Code Management
8. Configura:

   | Campo | Valor |
   |-------|-------|
   | **Project Repository** | URL de tu repo (ej: `https://github.com/Angel142330/curso-jenkins.git`) |
   | **Library Path** | `jenkins-shared-library/` |

9. Si tu repositorio es privado, agrega las credenciales
10. Haz clic en **Save** (Guardar)

#### 3. Usar en un Pipeline

Ahora puedes usar la biblioteca en cualquier Jenkinsfile:

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

### Opción 2: Uso Local (Para desarrollo/testing)

Esta opción carga la biblioteca directamente desde el mismo repositorio, útil para testing.

#### 1. Configurar el Pipeline en Jenkins

1. Crea un nuevo Pipeline en Jenkins
2. En la configuración del Pipeline:
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: URL de tu repositorio
   - **Branch**: `*/develop`
   - **Script Path**: `mi-proyecto/Jenkinsfile.library`

#### 2. El Jenkinsfile puede cargar la biblioteca así:

```groovy
// Opción A: Si está en el mismo repositorio
library identifier: 'jenkins-shared-library@develop', 
        retriever: modernSCM([
            $class: 'GitSCMSource',
            remote: 'https://github.com/Angel142330/curso-jenkins.git'
        ])

// Opción B: Cargar desde una ruta local en el workspace
// (Requiere que el repositorio ya esté clonado)
@Library('jenkins-shared-library@develop') _
```

### Opción 3: Biblioteca en el mismo repositorio del proyecto

Si la biblioteca está en el mismo repo que tu proyecto:

```groovy
// Jenkinsfile
library identifier: 'jenkins-shared-library@develop', 
        retriever: modernSCM([
            $class: 'GitSCMSource',
            remote: env.GIT_URL,  // Usa la URL del repositorio actual
            credentialsId: 'tu-credential-id'  // Si es necesario
        ])

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

## 🧪 Probar la Configuración

### 1. Pipeline Simple de Prueba

Crea un nuevo Pipeline con este código:

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Test Library') {
            steps {
                script {
                    echo '✅ Shared Library cargada correctamente!'
                    notifySlack('Prueba de biblioteca', 'SUCCESS')
                }
            }
        }
    }
}
```

### 2. Verificar que se cargó

En la consola de salida del build deberías ver:

```
Loading library jenkins-shared-library@develop
```

Si ves este mensaje, ¡la biblioteca se cargó correctamente! ✅

## 🔍 Troubleshooting

### Error: "Library not found"

**Causa**: La biblioteca no está configurada o el nombre es incorrecto.

**Solución**:
- Verifica que el nombre en `@Library('...')` coincida con el configurado en Jenkins
- Verifica que la rama especificada existe

### Error: "No such DSL method"

**Causa**: La función que intentas usar no existe en vars/

**Solución**:
- Verifica que el archivo existe en `jenkins-shared-library/vars/`
- El nombre del archivo debe ser `nombreFuncion.groovy`
- Verifica que tiene el método `def call(...)`

### Error: "Script not yet approved"

**Causa**: Jenkins requiere aprobación de scripts para seguridad.

**Solución**:
1. Ve a **Administrar Jenkins** → **In-process Script Approval**
2. Aprueba los scripts pendientes

### Error al cargar desde el mismo repositorio

**Causa**: Conflicto de checkout o permisos.

**Solución**:
- Usa la configuración global en lugar de cargar desde el mismo repo
- O especifica una rama/tag específico: `@Library('jenkins-shared-library@v1.0.0')`

## 📝 Ejemplos de Uso

### Ejemplo 1: Pipeline Maven Simple

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
}
```

### Ejemplo 2: Pipeline con Deploy Condicional

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
        stage('Deploy Production') {
            when {
                branch 'main'
            }
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

### Ejemplo 3: Múltiples Proyectos

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build Backend') {
            steps {
                buildMaven('backend')
            }
        }
        stage('Build Frontend') {
            steps {
                dockerBuild('frontend', "${env.BUILD_NUMBER}")
            }
        }
        stage('Test Backend') {
            steps {
                testMaven('backend')
            }
        }
    }
}
```

## 🔄 Actualizar la Biblioteca

Cuando hagas cambios en la shared library:

1. Haz commit y push de los cambios:
   ```bash
   git add jenkins-shared-library/
   git commit -m "Update shared library"
   git push origin develop
   ```

2. Los pipelines usarán automáticamente la nueva versión en el próximo build
   (si usas la configuración global con la rama `develop`)

3. Para versiones específicas, usa tags:
   ```bash
   git tag -a v1.0.0 -m "Version 1.0.0"
   git push origin v1.0.0
   ```
   
   Y úsalo así:
   ```groovy
   @Library('jenkins-shared-library@v1.0.0') _
   ```

## 🎯 Próximos Pasos

1. ✅ Configura la biblioteca en Jenkins (Global o Local)
2. ✅ Prueba con un pipeline simple
3. ✅ Convierte tus Jenkinsfiles existentes para usar la biblioteca
4. ✅ Agrega nuevas funciones según tus necesidades
5. ✅ Versiona la biblioteca con tags para mayor control

---

¿Necesitas ayuda? Revisa los logs de Jenkins o consulta la [documentación oficial](https://www.jenkins.io/doc/book/pipeline/shared-libraries/).

