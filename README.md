Приложение должно представлять собой клиент-серверную справочную систему.

Функциональные требования:

1. Приложение-клиент
	.1 Подключается к серверу по связке IP адреса и порта
	.2 Запрашивает у пользователя логин и пароль
	.3 Обеспечивает пользователю возможность подключиться к базе данных по выбору
	.4 Предоставляет пользователю возможность осуществлять поиск, добавление, удаление и редактирование объектов (в зависимости от уровня доступа пользователя)
	
2. Приложение-серверну
	.1 Имеет консольный интерфейс с возможностью создания, редактирования, удаления, импортирования, экспортирования и слияния баз данных об объектах, создания, редактирования и удаления пользователей, имеющих возможность подключаться к базам через приложение-клиент.
	.2 Работает с приложениями-клиентами в многопоточном режиме
	.3 Хранит информацию о последнем подключении пользователя и взаимодействет с ними в соответствии с уровнем доступа пользователя
	.4 Генерирует Hash-коды для каждой версии баз данных, хранящихся на сервере

3. Клиент-серверное взаимодействие
	.1 Пользователь, подключающийся через приложение-клиент может иметь два уровня доступа: "Только чтение" и "Администрирование".
	.2 Пользователь, имеющий доступ "Только чтение", при подключении, выбирает, из доступных ему, базу, с которой будет работать и целиком скачивает её. Пользователь имеет возможности поиска по скаченной базе и просмотра информации об объектах. Если пользователь в процессе работы не отключался от сервера, сервер отправляет ему информацию об изменении базы в режиме реального времени. Если пользователь отключался от сервера и переподключился, сервер сравнивает последнюю переданную клиенту версию базы с текущей, в случае отличия клиент скачивает базу заново. Если база у клиента актуальна - работа с клиентом продолжается как если бы клиент не отключался.
	.3 Пользователь, имеющий уровень доступа "Администрирование" работает с базами данных без полного скачивания в оперативную память, в режиме реального времени и имеет возможность создавать, удалять и редактировать объекты в этих базах. 