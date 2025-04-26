plugins {
    alias(libs.plugins.android.application)  // Plugin de aplicación Android
    alias(libs.plugins.kotlin.android)       // Plugin de Kotlin para Android
    id("com.google.gms.google-services")     // Plugin de servicios de Google (Firebase, etc.)
}

android {
    namespace = "com.colegio.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.colegio.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true  // Habilita ViewBinding
    }
}

dependencies {

    //  Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0")) // BOM: gestiona versiones compatibles entre sí
    implementation(libs.google.firebase.analytics)             // Analytics para eventos
    implementation(libs.google.firebase.auth)                  // Autenticación (correo, Google, etc.)
    implementation("com.google.firebase:firebase-firestore")            // Base de datos Firestore
    implementation("com.google.firebase:firebase-perf")


    //  Imágenes y UI
    implementation("com.squareup.picasso:picasso:2.71828")              // Carga y cacheo de imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")            // Otra librería de imágenes (más poderosa)
    implementation("de.hdodenhof:circleimageview:3.1.0")                // Imagen circular (por ejemplo, para perfiles)
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")        // Slider de imágenes (por ejemplo, para noticias)
    implementation ("com.google.android.material:material:1.5.0")      // Material Design para UI
    implementation ("com.wdullaer:materialdatetimepicker:4.2.3")


    //  Autenticación con Google
    implementation("com.google.android.gms:play-services-auth:20.1.0")  // Login con cuenta Google

    //  Componentes de UI
    implementation("androidx.recyclerview:recyclerview:1.2.1")          // RecyclerView para listas y tarjetas
    implementation ("androidx.cardview:cardview:1.0.0")

    //  Librerías de Jetpack/AndroidX esenciales
    implementation(libs.androidx.core.ktx)             // Kotlin Extensions
    implementation(libs.androidx.appcompat)            // Compatibilidad con versiones antiguas
    implementation(libs.material)                      // Componentes Material Design
    implementation(libs.androidx.activity)             // Manejo de Activity
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)     // ConstraintLayout para layouts complejos

    //  Testing
    testImplementation(libs.junit)                          // JUnit para pruebas unitarias
    androidTestImplementation(libs.androidx.junit)          // JUnit para Android
    androidTestImplementation(libs.androidx.espresso.core)  // Pruebas UI con Espresso
}
