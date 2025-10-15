# 🗄️ Guía de Uso: Acceso a Bases de Datos SQL desde Jenkins

Esta guía muestra cómo usar la Shared Library para trabajar con bases de datos SQL desde pipelines de Jenkins.

## 📁 Archivos Creados

```
curso-jenkins/
├── src/org/miempresa/
│   └── DatabaseHelper.groovy       # Clase principal para BD
│
├── vars/
│   ├── sqlQuery.groovy             # Ejecutar SELECT
│   ├── sqlExecute.groovy           # Ejecutar INSERT/UPDATE/DELETE
│   └── sqlBackup.groovy            # Backup de tablas
│
├── resources/
│   └── db-config.json              # Configuración de BD por entorno
│
└── ejemplos/
    ├── Jenkinsfile-database-query      # Consultas SQL
    ├── Jenkinsfile-database-backup     # Backups automáticos
    └── Jenkinsfile-database-migration  # Migraciones de esquema
```

---

## 🔧 Prerequisitos

### 1. Drivers JDBC

Necesitas agregar los drivers JDBC a Jenkins. Hay varias formas:

#### Opción A: Agregar JAR globalmente en Jenkins

1. Descarga el driver JDBC:
   - PostgreSQL: https://jdbc.postgresql.org/download/
   - MySQL: https://dev.mysql.com/downloads/connector/j/
   - SQL Server: https://docs.microsoft.com/sql/connect/jdbc/

2. Copia el JAR a Jenkins:
   ```bash
   # En el servidor Jenkins:
   cp postgresql-42.x.x.jar /var/jenkins_home/lib/
   ```

3. Reinicia Jenkins

#### Opción B: Usar @Grab (Groovy Grape)

En tu Jenkinsfile:
```groovy
@Grab('org.postgresql:postgresql:42.5.0')
import groovy.sql.Sql
```

#### Opción C: Usar plugin Pipeline Utility Steps

Instala el plugin y usa `withClasspath`:
```groovy
withClasspath(['/path/to/postgresql.jar']) {
    // Tu código aquí
}
```

### 2. Credenciales en Jenkins

Crea credenciales para las contraseñas de BD:

1. Jenkins → Administrar Jenkins → Credentials
2. Add Credentials
3. Kind: Secret text
4. ID: `postgres-password`, `mysql-password`, etc.

---

## 📖 Uso Básico

### 1️⃣ Ejecutar Query (SELECT)

```groovy
@Library('jenkins-shared-library@develop') _

pipeline {
    agent any
    
    environment {
        DB_PASSWORD = credentials('postgres-password')
    }
    
    stages {
        stage('Query Users') {
            steps {
                script {
                    def results = sqlQuery(
                        driver: 'org.postgresql.Driver',
                        url: 'jdbc:postgresql://localhost:5432/mydb',
                        user: 'admin',
                        password: env.DB_PASSWORD,
                        query: 'SELECT * FROM users WHERE active = true',
                        exportTo: 'users.csv'  // Opcional
                    )
                    
                    echo "Total usuarios: ${results.size()}"
                    
                    results.each { user ->
                        echo "User: ${user.name} - ${user.email}"
                    }
                }
            }
        }
    }
}
```

### 2️⃣ Ejecutar Comandos (INSERT/UPDATE/DELETE)

```groovy
@Library('jenkins-shared-library@develop') _

pipeline {
    agent any
    
    stages {
        stage('Update Database') {
            steps {
                script {
                    // Comando único
                    def rowsAffected = sqlExecute(
                        driver: 'org.postgresql.Driver',
                        url: 'jdbc:postgresql://localhost:5432/mydb',
                        user: 'admin',
                        password: env.DB_PASSWORD,
                        command: "UPDATE users SET last_login = NOW() WHERE id = 1"
                    )
                    
                    echo "${rowsAffected} filas actualizadas"
                }
            }
        }
    }
}
```

### 3️⃣ Transacciones (múltiples comandos)

```groovy
@Library('jenkins-shared-library@develop') _

pipeline {
    agent any
    
    stages {
        stage('Deploy Transaction') {
            steps {
                script {
                    sqlExecute(
                        driver: 'org.postgresql.Driver',
                        url: 'jdbc:postgresql://localhost:5432/mydb',
                        user: 'admin',
                        password: env.DB_PASSWORD,
                        command: [
                            "INSERT INTO deployments (version, started_at) VALUES ('v1.0', NOW())",
                            "UPDATE app_status SET deploying = true",
                            "INSERT INTO logs (message) VALUES ('Deployment started')"
                        ],
                        transaction: true  // Todo o nada (rollback automático si falla)
                    )
                }
            }
        }
    }
}
```

### 4️⃣ Backup de Tablas

```groovy
@Library('jenkins-shared-library@develop') _

pipeline {
    agent any
    
    stages {
        stage('Backup') {
            steps {
                script {
                    sqlBackup(
                        driver: 'org.postgresql.Driver',
                        url: 'jdbc:postgresql://localhost:5432/mydb',
                        user: 'admin',
                        password: env.DB_PASSWORD,
                        table: 'users',
                        backupFile: 'backups/users_backup.csv'
                    )
                }
            }
        }
    }
}
```

---

## 🚀 Uso Avanzado con DatabaseHelper

```groovy
@Library('jenkins-shared-library@develop') _
import org.miempresa.DatabaseHelper

def dbHelper = new DatabaseHelper(this)

pipeline {
    agent any
    
    stages {
        stage('Advanced Operations') {
            steps {
                script {
                    def sql = null
                    
                    try {
                        // Crear conexión
                        sql = dbHelper.createConnection([
                            driver: 'org.postgresql.Driver',
                            url: 'jdbc:postgresql://localhost:5432/mydb',
                            user: 'admin',
                            password: env.DB_PASSWORD
                        ])
                        
                        // Ejecutar query
                        def results = dbHelper.executeQuery(sql, 
                            'SELECT * FROM users WHERE created_at > NOW() - INTERVAL \'7 days\''
                        )
                        
                        echo "Usuarios nuevos: ${results.size()}"
                        
                        // Exportar a CSV
                        dbHelper.exportToCSV(results, 'new_users.csv')
                        
                        // Ejecutar comando
                        dbHelper.executeCommand(sql, 
                            "UPDATE users SET verified = true WHERE email_confirmed = true"
                        )
                        
                    } finally {
                        // Siempre cerrar conexión
                        dbHelper.closeConnection(sql)
                    }
                }
            }
        }
    }
}
```

---

## 🔒 Configuración por Entorno

Usa el archivo `resources/db-config.json`:

```groovy
@Library('jenkins-shared-library@develop') _
import org.miempresa.DatabaseHelper

def dbHelper = new DatabaseHelper(this)

pipeline {
    agent any
    
    parameters {
        choice(name: 'ENV', choices: ['dev', 'staging', 'production'])
    }
    
    stages {
        stage('Query') {
            steps {
                script {
                    // Cargar config del archivo
                    def dbConfig = dbHelper.getDbConfig(
                        'resources/db-config.json', 
                        params.ENV
                    )
                    
                    // Usar config
                    def results = sqlQuery(
                        driver: dbConfig.driver,
                        url: dbConfig.url,
                        user: dbConfig.user,
                        password: dbConfig.password,
                        query: 'SELECT COUNT(*) as total FROM users'
                    )
                    
                    echo "Total users en ${params.ENV}: ${results[0].total}"
                }
            }
        }
    }
}
```

---

## 🗃️ Bases de Datos Soportadas

### PostgreSQL
```groovy
driver: 'org.postgresql.Driver'
url: 'jdbc:postgresql://host:5432/database'
```

### MySQL
```groovy
driver: 'com.mysql.cj.jdbc.Driver'
url: 'jdbc:mysql://host:3306/database'
```

### SQL Server
```groovy
driver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
url: 'jdbc:sqlserver://host:1433;databaseName=database'
```

### Oracle
```groovy
driver: 'oracle.jdbc.OracleDriver'
url: 'jdbc:oracle:thin:@host:1521:SID'
```

---

## 🎯 Casos de Uso Reales

### 1. Pre-deployment DB Check
```groovy
stage('Check DB') {
    steps {
        script {
            def results = sqlQuery(
                ...
                query: 'SELECT version FROM schema_migrations ORDER BY version DESC LIMIT 1'
            )
            
            def dbVersion = results[0].version
            
            if (dbVersion < env.REQUIRED_VERSION) {
                error("DB version ${dbVersion} < required ${env.REQUIRED_VERSION}")
            }
        }
    }
}
```

### 2. Feature Flag desde BD
```groovy
stage('Check Feature Flag') {
    steps {
        script {
            def results = sqlQuery(
                ...
                query: "SELECT enabled FROM feature_flags WHERE name = 'new_feature'"
            )
            
            if (results[0].enabled) {
                echo "✅ Feature habilitada, desplegando..."
            } else {
                echo "⏸️ Feature deshabilitada, saltando deploy"
                return
            }
        }
    }
}
```

### 3. Registro de Deployment en BD
```groovy
post {
    always {
        script {
            sqlExecute(
                ...
                command: """
                    INSERT INTO deployments 
                    (version, environment, status, deployed_at, deployed_by)
                    VALUES 
                    ('${env.VERSION}', '${env.ENV}', '${currentBuild.result}', NOW(), 'jenkins')
                """
            )
        }
    }
}
```

---

## ⚠️ Consideraciones de Seguridad

1. **Nunca** pongas passwords en el código
   ```groovy
   ❌ password: 'mypassword'
   ✅ password: credentials('db-password')
   ```

2. Usa **credenciales de Jenkins**
   - Credentials → Add → Secret text

3. Usa **conexiones de solo lectura** para queries
   ```groovy
   user: 'readonly_user'  // Usuario con permisos limitados
   ```

4. **Valida inputs** antes de queries dinámicas
   ```groovy
   def safeTable = params.TABLE.replaceAll('[^a-zA-Z0-9_]', '')
   query: "SELECT * FROM ${safeTable}"
   ```

---

## 📝 Ejemplos Completos

Revisa los archivos en `ejemplos/`:
- `Jenkinsfile-database-query` → Consultas y exportación
- `Jenkinsfile-database-backup` → Backups automáticos
- `Jenkinsfile-database-migration` → Migraciones de esquema

---

## 🐛 Troubleshooting

### Error: ClassNotFoundException: org.postgresql.Driver
**Solución**: Instalar el driver JDBC en Jenkins

### Error: Connection refused
**Solución**: Verificar que la BD esté accesible desde Jenkins (firewall, network)

### Error: Authentication failed
**Solución**: Verificar credenciales en Jenkins

---

¡Listo para usar! 🚀

