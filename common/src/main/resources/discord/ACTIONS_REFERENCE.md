# Discord Actions Reference

Полное руководство по всем доступным действиям Discord команд.

## Базовые Сообщения

### send_message
Отправляет обычное сообщение в канал.
```yaml
- send_message:
    content: "Hello, World!"
```

### ephemeral_message
Отправляет временное сообщение, видимое только пользователю.
```yaml
- ephemeral_message:
    content: "This is private!"
```

### private_message
Отправляет личное сообщение пользователю.
```yaml
- private_message:
    content: "Check your DMs!"
```

## Embed (Встроенные Сообщения)

### embed
Создает красивое встроенное сообщение с форматированием.
```yaml
- embed:
    title: "Title"
    description: "Description text"
    color: "#FF5733"
    thumbnail: "https://example.com/thumb.png"
    image: "https://example.com/image.png"
    footer: "Footer text"
    author: "Author name"
    fields:
      - name: "Field 1"
        value: "Value 1"
        inline: true
      - name: "Field 2"
        value: "Value 2"
        inline: false
```

## Управление Ролями

### add_role
Добавляет роль пользователю.
```yaml
- add_role:
    role_id: "123456789012345678"
    target_option: "user"  # опционально
```

### remove_role
Удаляет роль у пользователя.
```yaml
- remove_role:
    role_id: "123456789012345678"
    target_option: "user"
```

### create_role
Создает новую роль на сервере.
```yaml
- create_role:
    name: "New Role"
    color: "#FF5733"
    hoisted: true
    mentionable: true
```

## Модерация

### kick
Кикает пользователя с сервера.
```yaml
- kick:
    target_option: "user"
    reason: "Violation of rules"
```

### ban
Банит пользователя на сервере.
```yaml
- ban:
    target_option: "user"
    reason: "Severe violation"
    delete_message_days: 7  # 0-7 дней
```

### timeout
Временно ограничивает пользователя (таймаут).
```yaml
- timeout:
    target_option: "user"
    duration_minutes: 10
    reason: "Spamming"
```

## Управление Каналами

### channel_message
Отправляет сообщение в конкретный канал.
```yaml
- channel_message:
    channel_id: "123456789012345678"
    content: "Announcement!"
```

### create_channel
Создает новый канал.
```yaml
- create_channel:
    name: "new-channel"
    type: "TEXT"  # TEXT или VOICE
```

### lock_channel
Блокирует/разблокирует канал.
```yaml
- lock_channel:
    lock: true  # true для блокировки, false для разблокировки
```

### slowmode
Устанавливает режим медленного чата.
```yaml
- slowmode:
    seconds: 10  # 0-21600 секунд
```

### delete_messages
Удаляет сообщения из канала.
```yaml
- delete_messages:
    amount: 50  # 1-100 сообщений
```

## Голосовые Каналы

### move_to_voice
Перемещает пользователя в голосовой канал.
```yaml
- move_to_voice:
    target_option: "user"
    voice_channel_id: "123456789012345678"
```

### mute
Мутит/размучивает пользователя в голосовом канале.
```yaml
- mute:
    target_option: "user"
    mute: true  # true для мута, false для размута
```

### deafen
Оглушает/разоглушает пользователя в голосовом канале.
```yaml
- deafen:
    target_option: "user"
    deafen: true
```

## Управление Пользователями

### nickname
Изменяет никнейм пользователя.
```yaml
- nickname:
    target_option: "user"  # опционально
    nickname: "NewNickname"  # null для сброса
```

## Сообщения и Реакции

### reaction
Добавляет реакцию к сообщению.
```yaml
- reaction:
    emoji: "👍"  # Unicode или <:name:id>
```

### pin_message
Закрепляет/открепляет сообщение.
```yaml
- pin_message:
    pin: true  # true для закрепления, false для открепления
```

## Треды

### create_thread
Создает тред (ветку обсуждения).
```yaml
- create_thread:
    name: "Discussion Thread"
    private: false  # true для приватного треда
```

## Интерактивные Компоненты

### buttons
Создает кнопки для взаимодействия.
```yaml
- buttons:
    message: "Choose an option:"
    buttons:
      - label: "Button 1"
        id: "button_1"
        style: "PRIMARY"  # PRIMARY, SUCCESS, DANGER, SECONDARY
        emoji: "👍"
      - label: "Link Button"
        url: "https://example.com"
        emoji: "🔗"
```

**Стили кнопок:**
- `PRIMARY` / `BLUE` - Синяя кнопка
- `SUCCESS` / `GREEN` - Зеленая кнопка
- `DANGER` / `RED` - Красная кнопка
- `SECONDARY` / `GRAY` / `GREY` - Серая кнопка

### select_menu
Создает выпадающее меню выбора.
```yaml
- select_menu:
    message: "Select an option:"
    id: "menu_id"
    placeholder: "Choose..."
    options:
      - label: "Option 1"
        value: "opt1"
        description: "Description"
        emoji: "1️⃣"
      - label: "Option 2"
        value: "opt2"
        description: "Another option"
        emoji: "2️⃣"
```

### modal
Создает модальное окно с формой ввода.
```yaml
- modal:
    id: "modal_id"
    title: "Form Title"
    inputs:
      - id: "input_1"
        label: "Short Input"
        style: "SHORT"  # SHORT или PARAGRAPH
        placeholder: "Enter text..."
        value: "Default value"
        required: true
      - id: "input_2"
        label: "Long Input"
        style: "PARAGRAPH"
        placeholder: "Enter long text..."
        required: false
```

## Примеры Комплексных Команд

### Команда Приветствия
```yaml
welcome:
  description: "Welcome new members"
  trigger: "welcome"
  actions:
    - embed:
        title: "Welcome!"
        description: "Glad to have you here!"
        color: "#00FF00"
    - add_role:
        role_id: "123456789012345678"
    - private_message:
        content: "Welcome to our server!"
    - reaction:
        emoji: "👋"
```

### Команда Модерации
```yaml
moderate:
  description: "Moderate user"
  trigger: "moderate"
  actions:
    - timeout:
        target_option: "user"
        duration_minutes: 30
        reason: "Warning"
    - channel_message:
        channel_id: "123456789012345678"
        content: "User has been moderated"
    - ephemeral_message:
        content: "Action completed"
```

### Интерактивная Команда
```yaml
interactive:
  description: "Interactive command"
  trigger: "interactive"
  actions:
    - embed:
        title: "Choose Your Action"
        description: "Select what you want to do"
        color: "#FF00FF"
    - buttons:
        message: "Quick actions:"
        buttons:
          - label: "Get Help"
            id: "help"
            style: "PRIMARY"
          - label: "Report"
            id: "report"
            style: "DANGER"
    - select_menu:
        message: "Or choose from menu:"
        id: "menu"
        placeholder: "Select..."
        options:
          - label: "Option A"
            value: "a"
          - label: "Option B"
            value: "b"
```

## Требуемые Разрешения

Некоторые действия требуют определенных разрешений:

- **kick**: `KICK_MEMBERS`
- **ban**: `BAN_MEMBERS`
- **timeout**: `MODERATE_MEMBERS`
- **add_role/remove_role**: `MANAGE_ROLES`
- **create_role**: `MANAGE_ROLES`
- **create_channel**: `MANAGE_CHANNEL`
- **lock_channel**: `MANAGE_CHANNEL`
- **slowmode**: `MANAGE_CHANNEL`
- **delete_messages**: `MESSAGE_MANAGE`
- **pin_message**: `MESSAGE_MANAGE`
- **move_to_voice**: `VOICE_MOVE_OTHERS`
- **mute**: `VOICE_MUTE_OTHERS`
- **deafen**: `VOICE_DEAF_OTHERS`
- **nickname**: `NICKNAME_MANAGE`
- **create_thread**: `CREATE_PUBLIC_THREADS` или `CREATE_PRIVATE_THREADS`

## Переменные и Плейсхолдеры

В будущих версиях будут доступны переменные:
- `{user}` - Упоминание пользователя
- `{user.name}` - Имя пользователя
- `{user.id}` - ID пользователя
- `{guild.name}` - Название сервера
- `{channel.name}` - Название канала
- `{timestamp}` - Текущее время

## Советы

1. **Комбинируйте действия** для создания сложных команд
2. **Используйте ephemeral_message** для приватных ответов
3. **Добавляйте реакции** для визуальной обратной связи
4. **Проверяйте разрешения** перед использованием модерационных команд
5. **Используйте embed** для красивого форматирования
6. **Создавайте интерактивные команды** с кнопками и меню
