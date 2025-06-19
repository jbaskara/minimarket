# Mini Market

**Mini Market** è un progetto Java strutturato secondo il pattern **MVC (Model-View-Controller)**, progettato per la gestione e la vendita di prodotti all’interno di un contesto monolitico. Il sistema supporta sia utenti **Clienti** che **Admin**, garantendo funzionalità gestionali e operative complete, dalla registrazione all’acquisto.

---

## Struttura del Progetto

```
minimarket
└── src/
    └── main/
        ├── java/
        │   ├── controller/
        │   │   ├── AuthController.java
        │   │   ├── CartController.java
        │   │   ├── OrderController.java
        │   │   └── ProductController.java
        │   ├── dao/
        │   │   ├── CartDAO.java
        │   │   ├── OrderDAO.java
        │   │   ├── ProductDAO.java
        │   │   └── UserDAO.java
        │   ├── db/
        │   │   └── DBConnection.java
        │   ├── model/
        │   │   ├── CartItem.java
        │   │   ├── Order.java
        │   │   ├── OrderItem.java
        │   │   ├── Product.java
        │   │   └── User.java
        │   ├── utils/
        │   │   └── PasswordUtils.java
        │   └── view/
        │       ├── CarrelloPanel.java
        │       ├── GestioneProdottiPanel.java
        │       ├── LoginPanel.java
        │       ├── LogoutPanel.java
        │       ├── Main.java
        │       ├── MenuAdminPanel.java
        │       ├── MenuClientiPanel.java
        │       ├── OrdiniPanel.java
        │       ├── RegistrazionePanel.java
        │       └── VisualizzazioneProdottiPanel.java
        └── resources/
            └── minimarket.sql
    └── test/
        └── db/
            └── DBConnectionTest.java
    └── test/resources/
    └── JRE System Library [JavaSE-24]
    └── pom.xml
```
---

## Funzionalità Principali

- **Login e Registrazione utenti** (Cliente e Admin univoco)
- **Gestione ruoli:** Cliente/Admin con privilegi distinti
- **Sicurezza:** Password criptate con BCrypt
- **Visualizzazione prodotti (Clienti):** Ricerca, filtro per colonne, inserimento quantità, selezione multipla
- **Carrello (Clienti):** Rimozione prodotti, acquisto con conferma
- **Gestione prodotti (Admin):** Inserimento, modifica e cancellazione prodotti
- **Storico ordini:** Visualizzazione dettagli acquisto per clienti e admin

---

## Funzionalità Database

- **Ruoli con privilegi:** Gestione Clienti e Admin tramite privilegi specifici
- **Tabelle principali:** Utenti, Prodotti, Ordini, Ordine_Prodotti, Carrello
- **Funzioni e trigger:** Per decremento/ripristino quantità prodotto, prevenzione azioni non autorizzate lato admin
- **Procedure/transazioni:** Gestione acquisto prodotti in modo atomico

---

## Requisiti

- **JDK** (consigliato Java 17+)
- **Eclipse IDE**
- **PostgreSQL**

---

## Procedura di Installazione

1. **Clonare o importare il progetto** in Eclipse IDE.
2. **Importare il database**:
   - Aprire il terminale/cmd e digitare:
     ```
     psql -U postgres -f "percorso/del/file/minimarket.sql"
     ```
     Esempio:
     ```
     psql -U postgres -f "C:\Users\utente\Downloads\minimarket.sql"
     ```
3. **Creazione account Admin**:
   - Modificare la riga 111 nella classe `RegistrazionePanel.java` cambiando `user.setAdmin(false)` in `user.setAdmin(true)` per il primo account.
   - Una volta creato l’admin, ripristinare il valore a `false` per evitare la creazione accidentale di altri admin.
4. **Configurare le credenziali di accesso al database** in `DBConnection.java` se necessario.

---

## Note Utili

- **Maven**: assicurarsi che tutte le dipendenze siano risolte tramite il comando Maven `Update Project`.
- **Sicurezza:** le password sono gestite e memorizzate in modo sicuro tramite BCrypt.
- **Database:** eventuali modifiche alle tabelle o ai trigger devono essere eseguite con attenzione per non compromettere la coerenza dei dati.

---

## Autore

- Jhoseph Baskara

---

## Licenza

Questo progetto è rilasciato sotto licenza MIT.  
Consulta il file [`LICENSE`](LICENSE) per maggiori dettagli.