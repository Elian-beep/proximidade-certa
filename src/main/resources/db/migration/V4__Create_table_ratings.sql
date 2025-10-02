CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    score INT NOT NULL CHECK (score >= 1 AND score <= 5),
    comment TEXT,
    author_name VARCHAR(100),
    establishment_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ratings_establishments
        FOREIGN KEY (establishment_id)
        REFERENCES establishments(id)
        ON DELETE CASCADE
);

-- Indice na chave estrangeira para melhorar a performance de buscas
CREATE INDEX idx_ratings_establishment_id ON ratings(establishment_id);