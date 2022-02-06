## `Статус сборки` [![Build status](https://ci.appveyor.com/api/projects/status/rdegan0naj2f4tje?svg=true)](https://ci.appveyor.com/project/Lognestix/aqa-exercise-2-3-2)
# Репортинг (AQA_Exercise_2.3-2)
## Домашнее задание по курсу "Автоматизированное тестирование"
## Тема: «2.3. Patterns», задание №2: «Тестовый режим»
- Применены инструменты:
	- Selenide;
	- Rest Assured;
	- Gson;
	- Faker.
- Реализованы:
	- Data-класс;
	- утилитный класс-генератор данных.
- Тестируемая функциональность:
	- вход через Web интерфейс с использованием Selenide.
### Предварительные требования
- На компьютере пользователя должна быть установлена:
	- Intellij IDEA
### Установка и запуск
1. Склонировать проект на свой компьютер
	- открыть терминал
	- ввести команду 
		```
		git clone https://github.com/Lognestix/AQA_Exercise_2.3-2
		```
1. Открыть склонированный проект в Intellij IDEA
1. В Intellij IDEA перейти во вкладку Terminal (Alt+F12) и запустить SUT командой
	```
	java -jar artifacts/app-ibank.jar -P:profile=test
	```
1. Запустить авто-тесты В Intellij IDEA во вкладке Terminal открыв еще одну сессию, ввести команду
	```
	./gradlew clean test
	```