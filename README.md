# Oshibki_novich
<h1 align="center">Календарь/Calendar</a> </h1>
<h3 align="center">Далее будут описаны способы запуска проекта, а также документация по его использованию.</h3>
<h2 align="center">Запуск приложения/Launching the app</a></h2>


![#f8f8f8](https://via.placeholder.com/10/f03c15?text=+)
Внимание! Для запуска и сборки можно использовать любой IDE, поддерживающий Java/Java Spring.
![#f8f8f8](https://via.placeholder.com/15/f03c15?text=+)


<p>Чтобы протестировать работу бота Вам необходимо:</p>
<ul>
<li> В папку /resources/application.properties (если она ещё не создана, то создать) загрузить токен и имя Вашего бота с названиями bot.token и bot.name соответственно.</li>
<li> В классе PostgresDBAdapter переопределить имя_пользователя, пароль и URL для доступа к базе данных.</li>
<li>В случае, если предыдущий шаг был выполнен успешно, нужная структура в базе данных создастся автоматически при запуске программы.</li>
<li>Если при запуске возникает ошибка, связанная с базой данных, попробуйте создать нужную структуру вручную, взяяв запрос из функции createTable класса PostgressDBAdapter</li>
<li> После настройки конфигураций можно нажимать 'Run 'app'' (зеленый треугольник в верхней панели инструментов).</li>
<li> Для запуска бота в Telegram, прописываем команду /start.</li>
</ul>

<h2 align="center">Документация/Documentation</h2>
<p>В данном видеоролике показан весь функционал бота на момент выхода документации.</p>
<p>Вот перечень фунций, которые может выполнять бот:</p>
<ul>
<li> Создавать (выбор даты, время, события, длительность выполнения), Удалять, Изменять события</li>
<li> Вывод всех событий </li>
<li> Вывод актуальных событий на сегодня и завтра </li>
<li> Поиск события по ключевым словам и дате, в том числе события раньше/позже введенной даты</li>
<li> Определение конфликтов при создании события и автоустранение их</li>
</ul>

<p>Инструкция по использыванию:</p>
<img src="https://github.com/Elizabeth-lapa/Oshibki_novichka/blob/develop/gif/view-bot.gif"/>

