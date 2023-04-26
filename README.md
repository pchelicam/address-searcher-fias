## address-searcher-fias

<b>address-searcher-fias</b> представляет собой REST приложение для поиска адресов по базе данных
ФИАС: https://fias.nalog.ru/Updates

Приложение работает с Postgres. Необходимо создать БД `fiasdb`, настроить username и password в `application.yml`, при
первом запуске приложения будет создана схема `fias` в `fiasdb`.

Для запуска приложения необходимо ввести:

```
./gradlew bootRun
```

В приложении доступны 4 endpoint-а:

```
http://localhost:8090/addressSearcher/locality
http://localhost:8090/addressSearcher/street
http://localhost:8090/addressSearcher/house
http://localhost:8090/addressSearcher/apartment
```

Пример `GET` запроса:

```
http://localhost:8090/addressSearcher/locality?regionCode=64&name=Саратов
```

По умолчанию пути к импортируемым файлам к данным ФИАСа хранятся в

```
E:/gar_xml/
E:/gar_delta_xml/
```

данные значения можно изменить через endpoint

```
http://localhost:8090/addressSearcher/config
```

через метод `POST`, передавая JSON вида:

```
[
    {
        "name" : "path_to_xml_data",
        "value" : "E:/gar_xml/"
    },
    {
        "name" : "path_to_xml_data_updates",
        "value" : "E:/gar_delta_xml/"
    }
]
```