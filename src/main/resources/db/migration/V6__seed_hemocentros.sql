-- Catalogo inicial de hemocentros, migrado dodao in-memory que o app mantinha
-- em HemocentroDAO.java. E dado de referencia, nao dado de teste: o agendamento
-- depende de um hemocentro existente, entao precisa existir antes do primeiro
-- uso da API.

INSERT INTO endereco (logradouro, numero, bairro, cidade, estado, cep)
VALUES
    ('Av. Dr. Enéas de Carvalho Aguiar', '155', 'Cerqueira César', 'São Paulo', 'SP', '05403-000'),
    ('Rua Frei Caneca',                   '91',  'Centro',          'Rio de Janeiro', 'RJ', '20211-030'),
    ('Rua Arlindo Lúcio',                 '235', 'Cachorro',        'Belo Horizonte', 'MG', '30622-020'),
    ('Av. João B. G. da Silva',           '500', 'Batel',           'Curitiba', 'PR', '80730-000');

-- A capacidade de coleta e 0.0 e o funcionamento 24h e false em todos, porque
-- o app nunca preenchia os dois campos. O horario de funcionamento ainda nao
-- participa de nenhuma regra: e exibido, e nao validado.
INSERT INTO hemocentro (id, nome, telefone, email, horario_abertura, horario_fechamento,
                        funcionamento_24_horas, capacidade_coleta, endereco_id)
SELECT h.id, h.nome, h.telefone, h.email, h.abertura::time, h.fechamento::time,
       FALSE, 0, e.id
FROM (
    VALUES
        ('HEMO-001', 'Hemocentro Central de São Paulo', '(11) 3069-6000', 'hemosp@saude.sp.gov.br',
         '07:00', '18:00', 'Av. Dr. Enéas de Carvalho Aguiar', '155'),
        ('HEMO-002', 'Hemocentro do Rio de Janeiro',     '(21) 3855-1400', 'hemorio@ri.rj.gov.br',
         '08:00', '17:00', 'Rua Frei Caneca',                   '91'),
        ('HEMO-003', 'Hemocentro de Belo Horizonte',      '(31) 3273-5600', 'hemobh@mg.gov.br',
         '07:00', '18:00', 'Rua Arlindo Lúcio',                 '235'),
        ('HEMO-004', 'Hemocentro de Curitiba',            '(41) 3362-4500', 'hemopr@pr.gov.br',
         '08:00', '17:00', 'Av. João B. G. da Silva',           '500')
) AS h (id, nome, telefone, email, abertura, fechamento, logradouro, numero)
JOIN endereco e ON e.logradouro = h.logradouro AND e.numero = h.numero;