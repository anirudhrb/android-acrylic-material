# Acrylic material for Android
Acrylic is a Fluent Design System component that adds physical texture
(material) and depth to your app. To learn more refer:
1. https://docs.microsoft.com/en-us/windows/uwp/design/style/acrylic
2. https://medium.com/microsoft-design/science-in-the-system-fluent-design-and-material-42b4dc532c14

# How to use
First, generate an acrylic drawable using an existing background image
drawable:
```java
Drawable d = AcrylicMaterial
                .with(MainActivity.this)
                .background(R.drawable.background_image)
                .useDefaults()
                .generate();
```

Then use the drawable as a background where the effect is needed!
