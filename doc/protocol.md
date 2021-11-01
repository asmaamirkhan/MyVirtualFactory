# My Virtual Factory
## Tanıtım
- Sanal bir fabrika yönetimi simülasyonudur
- Sistem, merkezi bir sunucu ve 2 kullanıcı türünden oluşmaktadır
- Kullanıcılar, sunucuya bağlandıktan sonra sürekli mesaj iletişiminde bulunabilirler


## Kullanıcı (istemci) Türleri
| #   | Tür        | Açıklama                          | Kod |
| --- | ---------- | --------------------------------- | --- |
| 1   | Makine     | Fabrikada ağa bağlı iş makineleri | `1` |
| 2   | Planlamacı | Fabrika yöneticileri              | `2` |


## Bağlantı Detayları
- Sunucu, istekleri `1234` numaralı port üzerinden dinler
- Bütün istekler TCP protokolü ile yapılmalı

| Bilgi    | Değer  |
| -------- | ------ |
| Port     | `1234` |
| Protokol | TCP    |

## Sunucu Mesaj Yapısı
- Sunucu detayları 2 türe ayrılmakta

| Tür   | Açıklama                                                      | Alanlar        |
| ----- | ------------------------------------------------------------- | -------------- |
| Mesaj | Yaptırılan bir işleme cevap olarak gönderilen mesaj türüdür   | `data`, `code` |
| İstek | Kullanıcıya bir işlem yaptırmak için kullanılan mesaj türüdür | `opCode`       |

## Kullanıcı İstek Detayları
- Kullanıcı istekleri, sırası önemli olmaksızın `data`, `opCode`, `type` olmak üzere **tam** 3 alan içermek zorundadır
- Bütün değerler `String` olarak gönderilir
  
| Alan     | Açıklama                                                                         |
| -------- | -------------------------------------------------------------------------------- |
| `data`   | Sunucuya aktarılması gereken veridir, değer olmaması halinde `null` yazılmalıdır |
| `opCode` | Yapılacak işlemin kodu                                                           |
| `type`   | Kullanıcı tipi (kodu)                                                            |


## Mesaj Düzenleme Kuralları

| Sembol | Açıklama                                                                       |
| ------ | ------------------------------------------------------------------------------ |
| `,`    | Mesaj alanlarını ayırmak için kullanılır                                       |
| `:`    | Mesaj alan başlığı ve değerini ayırmak için kullanılır                         |
| `;`    | `data` alanındaki değerleri ayırmak için kullanılır                            |
| `?`    | `data` alanındaki ikili değerlerin başlık ve içeriğini ayırmak için kullanılır |
| `&`    | `data` alanındaki objelerin listesini ayırma için kullanılır                   |

> Örnek: `type:1,opCode:register,data:id?11;type?CNC;speed?12;name?mm`

## Makine Destekli İşlemler
- İş makinelerinin sunucu üzerine yapabildiği işlemler

| İşlem         | Açıklama                           |
| ------------- | ---------------------------------- |
| `register`    | Makinenin sunucuya bağlanması      |
| `disconnect`  | Makine bağlantısının kesilmesi     |
| `finishOrder` | İş emrinin bittiği bildirilmesidir |

### `register` İşlemi
- Gönderen: istemci
- Yeni bir makinenin, sunucuya bağlanmak için yapması gereken işlem
- Yeni bir makinenin kaydolabilmesi için, id, isim, tip ve hız bilgilerini vermelidir 
  - ID: Makinenin tekil ID bilgisi
  - İsim: Makinenin adı
  - Tip: makinenin türü  (CNC, DÖKÜM, KILIF, KAPLAMA gibi)
  - Hız: Dakika cinsinden makinanın birim işi "1Metre veya 1KG" bitirme zamanı 
- Bulunması gereken alanlar:
  
| Alan     | Değer                                           | Açıklama         |
| -------- | ----------------------------------------------- | ---------------- |
| `type`   | 1                                               | Kullanıcı türü   |
| `opCode` | `register`                                      | İşlem kodu       |
| `data`   | `id?<ID>;type?<TYPE>;speed?<SPEED>;name?<NAME>` | Makine bilgileri |

> Örnek: `type:1,opCode:register,data:id?11;type?CNC;speed?12;name?mm`

#### Response detayları

| Durum                    | `code` | `data`               |
| ------------------------ | ------ | -------------------- |
| Başarılı kayıt           | 200    | null                 |
| İstek yapı hatası        | 400    | Invalid request      |
| Makine bilgi yapı hatası | 403    | Invalid machine info |

> Örnek: `code:200,data:null`


### `disconnect` İşlemi
- Gönderen: istemci
- Makinenin bağlantı kesme isteğidir

| Alan     | Değer        | Açıklama       |
| -------- | ------------ | -------------- |
| `type`   | 1            | Kullanıcı türü |
| `opCode` | `disconnect` | İşlem kodu     |
| `data`   | null         | -              |

> Örnek: `type:1,opCode:disconnect,data:null`

#### Response detayları

| Durum             | `code` | `data`          |
| ----------------- | ------ | --------------- |
| Başarılı kayıt    | 200    | null            |
| İstek yapı hatası | 400    | Invalid request |


### `finishOrder` İşlemi
- Gönderen: istemci
- makinenin, sunucuya emrinin bittiğini bildirilmesidir
  
| Alan     | Değer         | Açıklama   |
| -------- | ------------- | ---------- |
| `opCode` | `finishOrder` | İşlem kodu |
| `data`   | null          | -          |
> Örnek: `type:1,opCode:finishOrder,data:null`


### `assignOrder` İşlemi
- Gönderen: sunucu
- Sunucunun, bir makineye emir atamasıdır
  
| Alan     | Değer                | Açıklama     |
| -------- | -------------------- | ------------ |
| `opCode` | `assignOrder`        | İşlem kodu   |
| `data`   | `duration?<DURATON>` | İşlem süresi |
> Örnek: `opCode:assignOrder,data:duration?20.0`

## Planlamacı Destekli İşlemler
- Planlamacıların sunucu üzerine yapabildiği işlemler

| İşlem                | Açıklama                                  |
| -------------------- | ----------------------------------------- |
| `login`              | Planlamacının giriş yapma işlemi          |
| `getAliveMachines`   | Aktif makinelerin listesinin çekilmesi    |
| `getAliveMachineIDs` | Aktif makinelerin ID listesinin çekilmesi |
| `setNewOrder`        | Yeni iş emrinin tanımlanması              |
| `getWaitingOrders`   | Bekleyen iş emirleri listesinin çekilmesi |
| `disconnect`         | Planlamacı bağlantısının kesilmesi        |

### `login` İşlemi
- Gönderen: istemci
- Planlamacının sunucu sistemine giriş yapma işlemidir
- Sistemde tanımlı kullanıcılar:
  - name: `asmaa`, password: `123`
  - name: `esma`, password: `123`

| Alan     | Değer                                      | Açıklama            |
| -------- | ------------------------------------------ | ------------------- |
| `type`   | 2                                          | Kullanıcı türü      |
| `opCode` | `login`                                    | İşlem kodu          |
| `data`   | `data:name?<USERNAME>;password?<PASSWORD>` | Kullanıcı bilgileri |

> Örnek: `type:2,opCode:login,data:name?esma;password?123`


#### Response detayları

| Durum                                       | `code` | `data`                     |
| ------------------------------------------- | ------ | -------------------------- |
| Başarılı giriş                              | 200    | null                       |
| İstek yapı hatası                           | 400    | Invalid request            |
| Hatalı şifre                                | 401    | wrong pass                 |
| Kullanıcının zaten giriş yapmış olma durumu | 403    | user has already logged in |


### `getAliveMachines` İşlemi
- Gönderen: istemci
- Aktif olarak sisteme bağlı olan makinelerin listesi

| Alan     | Değer              | Açıklama       |
| -------- | ------------------ | -------------- |
| `type`   | 2                  | Kullanıcı türü |
| `opCode` | `getAliveMachines` | İşlem kodu     |
| `data`   | null               | -              |

> Örnek: `type:2,opCode:getAliveMachineIDs,data:null`


#### Response detayları

| Durum             | `code` | `data`          |
| ----------------- | ------ | --------------- |
| Başarılı işlem    | 200    | Makine listesi  |
| İstek yapı hatası | 400    | Invalid request |

> Örnek: `code:200,data:name?qq;ID?12;type?CNC;speed?12;isBusy?false&name?ww;ID?3;type?CNC;speed?23;isBusy?false`

### `getAliveMachineIDs` İşlemi
- Gönderen: istemci
- Aktif olarak sisteme bağlı olan makinelerin ID listesi

| Alan     | Değer                | Açıklama       |
| -------- | -------------------- | -------------- |
| `type`   | 2                    | Kullanıcı türü |
| `opCode` | `getAliveMachineIDs` | İşlem kodu     |
| `data`   | null                 | -              |

> Örnek: `type:2,opCode:getAliveMachineIDs,data:null`


#### Response detayları

| Durum             | `code` | `data`                 |
| ----------------- | ------ | ---------------------- |
| Başarılı işlem    | 200    | Makinelerin ID listesi |
| İstek yapı hatası | 400    | Invalid request        |

> Örnek: `code:200,data:12&3`

### `setNewOrder` İşlemi
- Gönderen: istemci
- Yeni iş emrinin girilmesidir
- Yeni bir iş emrinin kaydedilebilmesi için, id, tip ve miktar bilgilerinin vermelidir 
  - ID: İş emrinin tekil ID bilgisi
  - Tip: İşin türü türü  (CNC, DÖKÜM, KILIF, KAPLAMA gibi)
  - Hız: Yapılacak işin miktarı 

| Alan     | Değer                                           | Açıklama       |
| -------- | ----------------------------------------------- | -------------- |
| `type`   | 2                                               | Kullanıcı türü |
| `opCode` | `setNewOrder`                                   | İşlem kodu     |
| `data`   | `id?<ID>;type?<ORDER_TYPE>;quantity?<QUANTITY>` | Emir bilgileri |

> Örnek: `type:2,opCode:setNewOrder,data:id?3;type?KAPLAMA;quantity?30`


#### Response detayları

| Durum                  | `code` | `data`             |
| ---------------------- | ------ | ------------------ |
| Başarılı işlem         | 200    | null               |
| İstek yapı hatası      | 400    | Invalid request    |
| Emir bilgi yapı hatası | 403    | Invalid order info |


> Örnek: `code:200,data:null`

### `getWaitingOrders` İşlemi
- Gönderen: istemci
- Beklemede olan iş emirlerinin listesi

| Alan     | Değer              | Açıklama       |
| -------- | ------------------ | -------------- |
| `type`   | 2                  | Kullanıcı türü |
| `opCode` | `getWaitingOrders` | İşlem kodu     |
| `data`   | null               | -              |

> Örnek: `type:2,opCode:getWaitingOrders,data:null`


#### Response detayları

| Durum             | `code` | `data`                 |
| ----------------- | ------ | ---------------------- |
| Başarılı işlem    | 200    | Makinelerin ID listesi |
| İstek yapı hatası | 400    | Invalid request        |


> Örnek: `code:200,data:ID?2;type?KILIF;quantity?1&ID?23;type?KAPLAMA;quantity?40`