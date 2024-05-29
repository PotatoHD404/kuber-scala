# K8s Autoscaler

[Русский](README.md) | [English](README_EN.md)

K8s Autoscaler - это проект, который использует K3s, Terraform и Scala для автоматического масштабирования кластера
Kubernetes в зависимости от потребности в ресурсах. В этом README приведены инструкции по настройке и запуску проекта.

## Начало работы

Для начала работы с проектом необходимо склонировать репозиторий:

```
git clone https://gitlab.com/PotatoHD/k8s-autoscaler.git
```

Если вы хотите вносить изменения в проект, необходимо сделать форк репозитория и создать merge request (pull request) с
вашими изменениями.

## Минимальная конфигурация

### Предварительные требования

Перед началом работы убедитесь, что на вашей системе установлен Docker. Для установки Docker следуйте инструкциям для
вашей операционной системы:

- **Windows**: [Установка Docker Desktop для Windows](https://docs.docker.com/docker-for-windows/install/)
- **macOS**: [Установка Docker Desktop для Mac](https://docs.docker.com/docker-for-mac/install/)
- **Linux**: [Установка Docker Engine](https://docs.docker.com/engine/install/)

### Конфигурация

1. Создайте файл `.env` в корневом каталоге проекта и укажите следующие переменные окружения:

   ```
   YC_TOKEN="your_yandex_cloud_token"
   YC_CLOUD_ID="your_yandex_cloud_id"
   YC_FOLDER_ID="your_yandex_folder_id"
   YC_ZONE="your_yandex_zone"
   K3S_TOKEN="your_k3s_token"
   S3_ACCESS_KEY="your_s3_access_key"
   S3_SECRET_KEY="your_s3_secret_key"
   ```

   Замените значения на свои фактические данные.

2. Сгенерируйте пару SSH-ключей (приватный и публичный) и сохраните публичный ключ в файле `id_rsa.pub` в корневом
   каталоге проекта. Для генерации ключей выполните следующую команду:

   ```
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```

   Замените `"your_email@example.com"` на ваш адрес электронной почты или любой другой идентификатор. Следуйте
   инструкциям для сохранения ключей.

### Запуск проекта

Для запуска проекта с помощью Docker Compose используйте следующие команды:

- Создание кластера:
  ```
  docker-compose up cluster-creator
  ```

- Удаление кластера:
  ```
  docker-compose up cluster-destroyer
  ```

После успешного создания кластера будут выведены публичные и внутренние IP-адреса виртуальных машин. Вы можете
использовать публичные IP-адреса для подключения к виртуальным машинам по SSH.

## Локальная сборка и запуск

### Предварительные требования

Для локальной сборки и запуска проекта убедитесь, что на вашей системе установлены следующие компоненты:

- Java Development Kit (JDK) 11 или выше
- Scala 2.13 или выше
- Scala Build Tool (SBT)
- IntelliJ IDEA (опционально, для локальной разработки)

### Установка JDK, Scala и SBT

1. Загрузите и установите Java Development Kit (JDK) 11 или выше
   с [официального сайта Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) или используйте
   менеджер пакетов для вашей операционной системы.

2. Установите Scala, следуя инструкциям на [официальном сайте Scala](https://www.scala-lang.org/download/).

3. Установите Scala Build Tool (SBT), следуя инструкциям
   на [официальном сайте SBT](https://www.scala-sbt.org/download.html).

### Установка IntelliJ IDEA (опционально)

Если вы предпочитаете разрабатывать локально с использованием IDE, вы можете установить IntelliJ IDEA, следуя этим
шагам:

1. Загрузите IntelliJ IDEA с [официального сайта JetBrains](https://www.jetbrains.com/idea/download/).
2. Следуйте инструкциям по установке для вашей операционной системы.

### Сборка и отправка образов Docker

1. Соберите образ Docker для автоскейлера с помощью следующей команды:

   ```
   docker build -t potatohd/autoscaler -f Dockerfile.autoscaler .
   ```

   Если вы хотите использовать свое собственное имя образа, замените `potatohd/autoscaler` на желаемое имя.

2. Соберите образ Docker для создателя кластера с помощью следующей команды:

   ```
   docker build -t potatohd/cluster-creator -f Dockerfile.cluster-creator .
   ```

   Если вы хотите использовать свое собственное имя образа, замените `potatohd/cluster-creator` на желаемое имя.

3. Отправьте собранные образы в реестр контейнеров, например, Docker Hub:

   ```
   docker push potatohd/autoscaler
   docker push potatohd/cluster-creator
   ```

   Замените `potatohd` на ваше имя пользователя Docker Hub или другой реестр контейнеров.

   Если вы используете свои собственные имена образов, обязательно обновите `image` в файле `helm/values.yaml` для
   автоскейлера и `image` в файле `docker-compose.yml` для создателя кластера.

### Сборка Helm-чарта

1. Перейдите в каталог `helm`:

   ```
   cd helm
   ```

2. Соберите Helm-чарт с помощью следующей команды:

   ```
   helm package .
   ```

   Будет создан архив с Helm-чартом, например, `k8s-autoscaler-0.1.0.tgz`.

3. Отправьте собранный Helm-чарт в репозиторий Helm или разместите его в месте, доступном для установки.

### Локальный запуск

1. Перейдите в корневой каталог проекта:

   ```
   cd k8s-autoscaler
   ```

2. Соберите проект с помощью SBT:

   ```
   sbt compile
   ```

3. Запустите класс `Main` в модуле `autoscaler`:

   ```
   sbt "autoscaler/run"
   ```

   Или откройте проект в IntelliJ IDEA, найдите класс `Main` в модуле `autoscaler` и запустите его.

## Конфигурация

Перед запуском проекта необходимо настроить необходимые переменные окружения и файлы.

### Переменные окружения

Создайте файл `.env` в корневом каталоге проекта и укажите следующие переменные окружения:

```
YC_TOKEN="your_yandex_cloud_token"
YC_CLOUD_ID="your_yandex_cloud_id"
YC_FOLDER_ID="your_yandex_folder_id"
YC_ZONE="your_yandex_zone"
K3S_TOKEN="your_k3s_token"
S3_ACCESS_KEY="your_s3_access_key"
S3_SECRET_KEY="your_s3_secret_key"
```

Прежде чем заменять значения, убедитесь, что у вас есть аккаунт в [Yandex Cloud](https://yandex.cloud/ru/) и вы создали
хотя бы одно облако и каталог в нем.

Замените значения на свои фактические данные:

- `YC_TOKEN`: Ваш API-токен Yandex Cloud. Вы можете получить его
  в [консоли управления Yandex Cloud](https://console.cloud.yandex.ru/cloud?section=overview). Перейдите в раздел "
  Сервисные аккаунты", создайте сервисный аккаунт с ролью admin и создайте новый API-ключ.
- `YC_CLOUD_ID`: Идентификатор вашего облака Yandex Cloud. Вы можете найти его
  в [консоли управления Yandex Cloud](https://console.cloud.yandex.ru/cloud?section=overview) сверху.
- `YC_FOLDER_ID`: Идентификатор каталога в вашем облаке Yandex Cloud, где будут создаваться ресурсы. Вы можете найти его
  в [консоли управления Yandex Cloud](https://console.cloud.yandex.ru/cloud?section=overview) в разделе "Каталоги".
- `YC_ZONE`: Зона Yandex Cloud, где будут создаваться ресурсы (например, `ru-central1-a`). Вы можете выбрать подходящую
  зону в [документации Yandex Cloud](https://yandex.cloud/ru/docs/overview/concepts/geo-scope) в разделе "Зоны
  доступности".
- `K3S_TOKEN`: Токен, используемый для аутентификации в кластере K3s. Вы можете сгенерировать случайный токен или
  использовать существующий (например `86cl4b19zfxrto2ybwnfi4nvqixfo1hqyyj77j9qiqetqr8k`).
- `S3_ACCESS_KEY`: Ключ доступа для вашего S3-совместимого хранилища. Вы можете получить его в вашей консоли облака
  Yandex Cloud для сервисного аккаунта, который вы уже создали. Создайте для него новый ключ доступа, скопируйте
  публичную часть, а приватную оставьте для следующего пункта.
- `S3_SECRET_KEY`: Секретный ключ для вашего S3-совместимого хранилища. Вставьте приватную часть полученную на
  предыдущем шаге.

### SSH-ключ

Создайте файл с именем `id_rsa.pub` в корневом каталоге проекта и вставьте в него ваш публичный SSH-ключ. Этот ключ
будет использоваться для подключения к виртуальным машинам в кластере.

Чтобы сгенерировать пару SSH-ключей (приватный и публичный), выполните следующие шаги:

1. Откройте терминал или командную строку на вашей локальной машине.
2. Выполните следующую команду для генерации пары SSH-ключей:
   ```
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```
   Замените `"your_email@example.com"` на ваш адрес электронной почты или любой другой идентификатор.
3. Когда будет предложено, нажмите Enter, чтобы принять расположение по умолчанию для сохранения пары
   ключей (`~/.ssh/id_rsa`).
4. При желании вы можете ввести пароль для дополнительной безопасности. Если вы не хотите устанавливать пароль, оставьте
   его пустым и нажмите Enter.
5. Пара SSH-ключей будет сгенерирована. У вас будет два файла:
    - `~/.ssh/id_rsa`: Приватный ключ. Храните этот файл в безопасности и не передавайте его никому.
    - `~/.ssh/id_rsa.pub`: Публичный ключ. Этот файл можно передавать и использовать для аутентификации.

Скопируйте содержимое файла `~/.ssh/id_rsa.pub` и вставьте его в файл `id_rsa.pub` в корневом каталоге проекта.

## Участие в проекте

Если вы хотите внести свой вклад в проект, пожалуйста, следуйте стандартному рабочему процессу GitLab:

1. Сделайте форк репозитория.
2. Создайте новую ветку для вашей функциональности или исправления ошибки.
3. Внесите изменения и зафиксируйте их с описательными сообщениями коммитов.
4. Отправьте изменения в свой форк репозитория.
5. Создайте merge request (pull request) в основной репозиторий.

## Лицензия

Этот проект распространяется под лицензией [MIT License](LICENSE.md).