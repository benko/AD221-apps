DROP TABLE IF EXISTS payment_analysis;
DROP TABLE IF EXISTS payments;

CREATE TABLE payments(
                         id INT NOT NULL AUTO_INCREMENT,
                         user_id INT NOT NULL,
                         amount DECIMAL NOT NULL,
                         order_id INT NOT NULL,
                         currency VARCHAR(3) NOT NULL,
                         email VARCHAR(1000) NOT NULL,

                         PRIMARY KEY (id)
);

INSERT INTO payments (user_id, amount, order_id, currency, email)
VALUES
    (11, 40.667, 8473, 'EUR', 'user11@example.mail.com'),
    (12, 500000.0, 8474, 'USD', 'user12@example.mail.com'),
    (13, 20.15, 8475, 'EUR', 'user13@example.mail.com'),
    (11, 400.45, 8478, 'EUR', 'this_is_suspicious----@.deals.offers.example.com');
