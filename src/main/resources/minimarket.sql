-- Crea i ruoli di login
CREATE ROLE minimarket_user LOGIN PASSWORD 'userpass';
CREATE ROLE minimarket_admin LOGIN PASSWORD 'adminpass';

-- Crea il database e assegna il proprietario
CREATE DATABASE minimarket OWNER minimarket_admin;

-- Concedi permessi
GRANT ALL PRIVILEGES ON DATABASE minimarket TO minimarket_admin;
GRANT CONNECT ON DATABASE minimarket TO minimarket_user;

-- Permessi sulle tabelle e schema (da dentro il database minimarket)
GRANT USAGE ON SCHEMA public TO minimarket_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO minimarket_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO minimarket_user;


-- Tabelle principali (utenti, prodotti, ordini, ordine prodotti e carrello)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL
);

CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id),
    product_id INT REFERENCES products(id),
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE cart (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    product_id INT REFERENCES products(id),
    quantity INT NOT NULL,
    purchased BOOLEAN DEFAULT FALSE
);


-- Inserimento dei prodotti
INSERT INTO products (name, category, quantity, price) VALUES
('Mela Golden', 'Frutta', 100, 1.20),
('Banana', 'Frutta', 120, 1.10),
('Insalata', 'Verdura', 80, 0.99),
('Pomodoro', 'Verdura', 90, 1.50),
('Petto di Pollo', 'Carne', 60, 7.90),
('Bistecca di Manzo', 'Carne', 40, 12.50),
('Filetto di Salmone', 'Pesce', 35, 16.00),
('Pane Integrale', 'Pane e prodotti da forno', 70, 2.30),
('Cornetto', 'Dolci, Snack e Pasticceria', 50, 1.00),
('Latte Intero', 'Latticini e Uova', 90, 1.50),
('Uova Fresche 6pz', 'Latticini e Uova', 80, 2.10),
('Prosciutto Cotto', 'Salumi e Affettati', 50, 2.90),
('Piselli Surgelati', 'Surgelati', 40, 1.80),
('Acqua Naturale 1.5L', 'Bevande', 200, 0.50),
('Pasta Spaghetti', 'Pasta, Riso e Cereali', 110, 1.20),
('Riso Arborio', 'Pasta, Riso e Cereali', 70, 2.00),
('Tonno in Scatola', 'Prodotti in scatola e Conserve', 60, 2.50),
('Olio Extra Vergine di Oliva', 'Condimenti e Spezie', 45, 5.30),
('Biscotti Secchi', 'Prodotti per la Colazione', 90, 1.70),
('Shampoo', 'Cura della persona', 30, 3.60),
('Detersivo per Piatti', 'Pulizia casa', 25, 2.40),
('Crocchette Cane 1kg', 'Alimenti per animali', 20, 4.90);


-- Funzione che decrementa la quantità prodotto
CREATE OR REPLACE FUNCTION decrementa_quantita_prodotti()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET quantity = quantity - NEW.quantity
    WHERE id = NEW.product_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger che chiama la funzione dopo l'INSERT sul carrello
CREATE TRIGGER trigger_decrementa_quantita
AFTER INSERT ON cart
FOR EACH ROW
EXECUTE FUNCTION decrementa_quantita_prodotti();


-- Funzione che ripristina la quantità prodotto
CREATE OR REPLACE FUNCTION ripristina_quantita_prodotti()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.purchased IS FALSE THEN
        UPDATE products
        SET quantity = quantity + OLD.quantity
        WHERE id = OLD.product_id;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger che chiama la funzione dopo il DELETE sul carrello
CREATE TRIGGER trigger_ripristina_quantita
AFTER DELETE ON cart
FOR EACH ROW
EXECUTE FUNCTION ripristina_quantita_prodotti();


-- Funzione di controllo admin singolo
CREATE OR REPLACE FUNCTION evita_secondo_admin()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_admin THEN
        IF (SELECT COUNT(*) FROM users WHERE is_admin) > 0 THEN
            RAISE EXCEPTION 'Non è consentito un altro admin.';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger su INSERT
CREATE TRIGGER trigger_evita_secondo_admin
BEFORE INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION evita_secondo_admin();

-- Trigger su UPDATE
CREATE TRIGGER trigger_evita_secondo_admin_update
BEFORE UPDATE OF is_admin ON users
FOR EACH ROW
WHEN (NEW.is_admin = TRUE AND OLD.is_admin = FALSE)
EXECUTE FUNCTION evita_secondo_admin();


-- Procedura acquista_prodotti
CREATE OR REPLACE PROCEDURE acquista_prodotti(p_user_id INT)
LANGUAGE plpgsql
AS $$
DECLARE
    total DECIMAL(10,2) := 0;
    ord_id INT;
BEGIN
    -- Avvia una transazione
    BEGIN
        -- Calcola il totale con una CTE
        WITH cart_total AS (
            SELECT SUM(c.quantity * p.price) AS totale
            FROM cart c
            JOIN products p ON c.product_id = p.id
            WHERE c.user_id = p_user_id
        )
        SELECT totale INTO total FROM cart_total;

        -- Inserisce l'ordine e recupera l'id
        INSERT INTO orders(user_id, total) VALUES (p_user_id, total) RETURNING id INTO ord_id;

        -- Inserisce le righe in order_items tramite CTE
        WITH user_cart AS (
            SELECT c.product_id, c.quantity, p.price
            FROM cart c
            JOIN products p ON c.product_id = p.id
            WHERE c.user_id = p_user_id
        )
        INSERT INTO order_items(order_id, product_id, quantity, price)
        SELECT ord_id, product_id, quantity, price FROM user_cart;

        -- Aggiorna e svuota il carrello
        UPDATE cart SET purchased = TRUE WHERE user_id = p_user_id;
        DELETE FROM cart WHERE user_id = p_user_id;

        -- Commit esplicito (facoltativo, perché la procedura di per sé è atomica)
        -- COMMIT;
    EXCEPTION WHEN OTHERS THEN
        -- In caso di errore rollback
        ROLLBACK;
        RAISE;
    END;
END;
$$;