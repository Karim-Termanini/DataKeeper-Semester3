# كيفية تشغيل اللعبة - How to Run

## ⚠️ مهم جداً!

اللعبة تحتاج الـ **resources** (أصوات وصور) تكون في مجلد `out`.

## 🚀 الطريقة الصحيحة للتشغيل

### Linux/Mac:
```bash
cd PlatformerExpanded
./build_and_run.sh
```

### Windows:
```
build_and_run.bat
```

هذه السكريبتات:
1. تمسح مجلد `out` القديم
2. تعيد compile الكود
3. **تنسخ كل الـ resources** للـ `out`
4. تشغل اللعبة

---

## 🎮 إذا كنت تستخدم IntelliJ IDEA

### الطريقة 1: استخدم السكريبت (موصى به)
1. افتح Terminal في IntelliJ
2. شغّل `./build_and_run.sh`

### الطريقة 2: إعداد IntelliJ
1. اذهب إلى **Run → Edit Configurations**
2. اختر **Main**
3. في **VM options** أضف:
   ```
   -cp out:res
   ```
4. أو في **Program arguments** أضف:
   ```
   -Djava.class.path=out:res
   ```
5. اعمل **Build → Rebuild Project**
6. شغّل اللعبة

### الطريقة 3: نسخ Resources يدوياً
```bash
cd PlatformerExpanded
rm -rf out
mkdir out
javac -d out -sourcepath src $(find src -name "*.java")
cp -r res/* out/
cd out
java main.Main
```

---

## ✅ التحديثات في هذه النسخة

### 1. القفزة أعلى بكثير
```java
jumpSpeed = -18.0f  // كان -14.0f
gravity = 0.5f      // كان 0.8f
```

### 2. الأصوات تعمل
- ✅ صوت ضرب اللاعب (hurt)
- ✅ صوت موت اللاعب (player_death)
- ✅ صوت ضرب العدو (hit)
- ✅ صوت موت العدو (enemy_death)
- ✅ صوت الهجوم (attack)
- ✅ صوت القفز (jump)
- ✅ صوت البوابة (portal)
- ✅ موسيقى خلفية

### 3. القائمة الرئيسية
- تبدأ اللعبة من القائمة الرئيسية
- W/S للتنقل
- ENTER للاختيار
- يظهر أعلى مرحلة وصلتها

---

## 🐛 إذا لم تعمل الأصوات

تأكد من:
1. مجلد `out/sounds` موجود
2. الملفات `.wav` موجودة في `out/sounds/player/`
3. شغّل من السكريبت `build_and_run.sh`

---

## 📁 هيكل المجلدات الصحيح

```
PlatformerExpanded/
├── src/           # الكود المصدري
├── res/           # الموارد الأصلية
├── out/           # الكود المترجم + نسخة من الموارد
│   ├── main/
│   ├── entities/
│   ├── sounds/    ← مهم!
│   ├── Enemy/     ← مهم!
│   └── ...
└── build_and_run.sh
```

---

## 🎯 الأوامر السريعة

### تنظيف وإعادة بناء كاملة:
```bash
cd PlatformerExpanded
rm -rf out
./build_and_run.sh
```

### فقط نسخ الموارد (إذا كان الكود مترجم):
```bash
cd PlatformerExpanded
cp -r res/* out/
cd out
java main.Main
```

---

**ملاحظة**: لا تشغل من IntelliJ مباشرة إلا إذا ضبطت الـ classpath صح!

