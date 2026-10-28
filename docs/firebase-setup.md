# إعداد Firebase للمتجر الحي

يرسل التطبيق بيانات المتجر (المستخدمون، المنتجات، الطلبات، النزاعات) إلى Firestore مع كل تغيير،
وتعرضها لوحة الويب مباشرة لمالك المتجر فقط.

## ما أُعدّ (23 أكتوبر 2026)

| البند | الحالة |
|---|---|
| المشروع | `ameraistore-1c81d` (AmERAiStore) — مشروع موجود أُعيد استخدامه |
| Firestore | قاعدة `(default)` في `nam5` |
| قواعد الأمان | `firestore.rules` منشورة. حلّت محل قواعد «وضع الاختبار» التي انتهت في 14 أغسطس وكانت تمنع الجميع |
| الدخول المجهول (Anonymous) | مفعّل، وبه يكتب التطبيق |
| النطاقات المسموح بها | أُضيف `mohammadzxzx1991.github.io` |
| تطبيق Android | «Amer Store (Android)» للحزمة `com.aistudio.aicommerce.kpqzjx`، وقيمه في `.env.example` |
| تطبيق الويب | «Amer Live Dashboard»، وإعداداته في `dashboard/firebase-config.js` |

اختُبر من الطرف إلى الطرف: الدخول المجهول يعمل، والكتابة في `products` تنجح، والقراءة دون حساب المالك مرفوضة،
والكتابة في أي مجموعة أخرى مرفوضة.

## المتبقي عليك (دقيقة واحدة)

**تفعيل الدخول بحساب Google** (للوحة):
https://console.firebase.google.com/project/ameraistore-1c81d/authentication/providers
← **Google** ← **Enable** ← اختر بريد الدعم ← **Save**.
هذه الخطوة تُنشئ عميل OAuth لا يمكن إنشاؤه من الأوامر.

GitHub Pages مفعّل (Source: GitHub Actions)، واللوحة تُنشر تلقائياً من `dashboard/` عبر `.github/workflows/pages.yml`.

## التجربة
1. ثبّت أحدث ملف APK من GitHub Actions، وافتح التطبيق مع اتصال بالإنترنت.
2. افتح https://mohammadzxzx1991.github.io/Mohammad-Ahmed-Ghannam/#store ← **تسجيل الدخول بـ Google** بحساب `zxzx.mohammad91@gmail.com`.
3. أضف طلباً في التطبيق، فيظهر في اللوحة خلال ثوانٍ.

## ملاحظات
- مفاتيح Firebase في المستودع ليست أسراراً، فهي داخل كل APK وكل صفحة ويب. الحماية من `firestore.rules`.
- أي نسخة مثبتة من التطبيق تستطيع الكتابة في مجموعات المتجر. هذا مقبول للتجربة، وقبل الإطلاق أضف Firebase App Check.
- لتغيير حساب المالك: عدّل البريد في `firestore.rules` ثم انشر القواعد من Firebase Console → Firestore → Rules.
- البيانات التي حُذفت من التطبيق وهو مغلق لا تُحذف من Firestore تلقائياً.
