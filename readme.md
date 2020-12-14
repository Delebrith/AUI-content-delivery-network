# Raport z projektu AUI
Projekt sieci CDN, analiza rozmieszczenia treści, przygotowanie środowiska testowego 
wykorzystując kontenery docker, zbadanie wydajności systemu. 

## Środowisko
### Serwery replik
Każdy z serwerów replik zostanie umieszczony w osobnym kontenerze docker i będzie zawierał w swojej
bazie danych podzbiór udostępnianych zasobów wskazany przez model. Serwer replik jest aplikacją 
opartą na bibliotece Springboot posiadającą własną instancję bazy danych H2 (baza in-memory). 

#### Zasoby
Zasób jest obrazem o przypisanym arbitralnie ID, który można uzyskać poprzez zapytanie HTTP GET, 
podając ID zasobu w URL. Przykładowe zasoby do późniejszych eksperymentów znajdują się w katalogu 
environment/resources.

#### Przekierowania
W przypadku nieznalezienia żadanego zasobu na konkrentym serwerze replik uruchomiony zostaje 
mechanizm przekierowań, przypominający uproszczony algorytm CHORD. Serwery replik implementują
go w następujący sposób:
1. Serwerom przyporządkowane są numery od 1 do n.
2. Każdy serwer zna zawartość m kolejnych serwerów. W przypadku serwera o numerze i, jeżeli:
```i + m > n```, serwer zna zawartość serwerów o numerach od 1 do ```m mod n```.
3. W przypadku nieznalezienia żądanego zasobu serwer przeszukuje tablicę zawartości m sąsiednich 
serwerów. Następnie przygotowuje link do przekierowania żądania do serwera, który posiada szukany 
zasób lub ostatniego z tablicy.
4. Serwer dołącza do zapytania ciasteczko z informacją, który serwer został odwiedzony i 
przekierowuje zapytanie.
5. Jeżeli serwer zostanie odwiedzony powtórnie, oznacza to, że wszystkie serwery replik zostały 
przeszukane i zasób nie znajduje się w systemie. Wtedy klientowi zwracana jest odpowiedź z kodem 
404 Not Found.

### Sieć
#### Struktura
Sieć CDN stanowić będzie sieć kontenerów typu docker zarządzana poprzez narzędzie Docker Compose.
Do celów eksperymentów ustaliliśmy sieć złożoną z 5 serwerów replik. Każdy z serwerów otrzyma
przyporządkowaną mu pulę klientów o wielkości zależnej od eksperymentu.

#### Inicjalizacja
Sieć uruchamiana jest poleceniem ```docker-compose up``` w katalogu /environment projektu. 
Komenda powoduje inicjalizację 5 kontenerów zawierających serwery replik. Na interfejsach
sieciowych kontenerów nakładane jest opóźnienie, które umożliwi zasymulowanie odległości
przy zapytaniach z przekierowaniami (polecenie ```tc```). Kontenerom należy zapewnić tablicę
zawierającą informacje o rozmieszczeniu zasobów w pliku konfiguracyjnym ```config.txt```.

Po uruchomieniu kontenerów należy wprowadzić do nich zasoby. W katalogu /environment znajduje się
skrypt w języku Python wykonujący zapytania typu PUT do serwerów replik, umieszczający zasoby
w odpowiednich bazach na podstawie tego samego pliku ```config.txt```. Po wykonaniu skryptu system
jest gotowy do testów.

### Klienci
Klienci sieci CDN zasymulowani zostaną poprzez scenariusz biblioteki Gatling z parametrami
określanymi przez eksperyment.

## Testowane modele

## Wyniki eksperymentów
