# Agora-Custom-Media-Device-Android


*Read this in other languages: [English](README.en.md)*

�����Դʾ����Ŀ��ʾ�����ʹ��Agora SDK���ṩ���µ�MediaIO�ӿ�

�����ʾ����Ŀ�У�ʹ��MediaIO�ӿ�ʵ���������������ܣ�

1��ʵ�ֹ����ֻ���Ļ����Ͷ���ض���View��Զ�ˡ�

2��ʵ�ֱ�����Ƶ��Camera��Ϊ�ⲿ����Դ��������ʾ��������Ƶ��Camera���ݵ�Զ�ˣ������µ�MediaIO�ӿڣ�ʵ��Ƶ������ƵԴ��̬�л���

�����������鿴Agora��ƵSDK������ʾ����Ŀ��[Agora-Android-Tutorial-1to1](https://github.com/AgoraIO/Agora-Android-Tutorial-1to1)

��Ҳ���Բ鿴 Android ƽ̨��ʹ�þɽӿ�ʵ����Ļ�����ʾ����Ŀ��[Agora-Screen-Sharing-Android](https://github.com/AgoraIO/Agora-Screen-Sharing-Android)

��Ҳ���Բ鿴 iOS ƽ̨��ʾ����Ŀ��[Agora-Screen-Sharing-iOS](https://github.com/AgoraIO/Agora-Screen-Sharing-iOS)

## ����ʾ������
**����**�� [Agora.io ע��](https://dashboard.agora.io/cn/signup/) ע���˺ţ��������Լ��Ĳ�����Ŀ����ȡ�� AppID���� AppID ��д�� "app/src/main/res/values/strings.xml"

```
<string name="agora_app_id"><#YOUR APP ID#></string>
```

**Ȼ��**�Ǽ��� Agora SDK�����ɷ�ʽ���������֣�

- ��ѡ���ɷ�ʽ��

����Ŀ��Ӧ��ģ��� "app/build.gradle" �ļ������������м���ͨ�� JCenter �Զ����� Agora SDK �ĵ�ַ��

```
compile 'io.agora.rtc:full-sdk:2.2.0'
```

(���Ҫ���Լ���Ӧ���м��� Agora ��Ƶ SDK��������ӵ�ַ������Ҫ��һ������

- ��ѡ���ɷ�ʽ��

��һ��: �� [Agora.io SDK](https://www.agora.io/cn/download/) ���� **��Ƶͨ�� + ֱ�� SDK**����ѹ�����е� **libs** �ļ����µ� ***.jar** ���Ƶ�����Ŀ�� **app/libs** �£����е� **libs** �ļ����µ� **arm64-v8a**/**x86**/**armeabi-v7a** ���Ƶ�����Ŀ�� **app/src/main/jniLibs** �¡�

�ڶ���: �ڱ���Ŀ�� "app/build.gradle" �ļ������������������������ϵ��

```
compile fileTree(dir: 'libs', include: ['*.jar'])
```

**���**�� Android Studio �򿪸���Ŀ�������豸�����벢���С�

Ҳ����ʹ�� `Gradle` ֱ�ӱ������С�

## ���л���
- Android Studio 2.0 +
- ��ʵ Android �豸 (Nexus 5X ���������豸)
- ����ģ��������ڹ���ȱʧ�����������⣬�����Ƽ�ʹ�����

## ��ϵ����

- ������ API �ĵ��� [�ĵ�����](https://docs.agora.io/cn/)
- ����ڼ�������������, ����Ե� [����������](https://dev.agora.io/cn/) ����
- �������ǰ��ѯ����, ���Բ��� 400 632 6626�������ٷ�QȺ 12742516 ����
- �����Ҫ�ۺ���֧��, ������� [Agora Dashboard](https://dashboard.agora.io) �ύ����
- ���������ʾ������� bug, ��ӭ�ύ [issue](https://github.com/AgoraIO/Agora-Screen-Sharing-Android/issues)

## �������

The MIT License (MIT).
