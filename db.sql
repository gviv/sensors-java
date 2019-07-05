CREATE TABLE building (
    id INT AUTO_INCREMENT,
    name VARCHAR(255),
    CONSTRAINT pk_building PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE fluid (
    id INT AUTO_INCREMENT,
    type VARCHAR(255),
    CONSTRAINT pk_fluid PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE sensor (
    id INT AUTO_INCREMENT,
    name VARCHAR(255),
    floor INT,
    place VARCHAR(255),
    fluid_id INT,
    building_id INT,
    min_threshold DOUBLE,
    max_threshold DOUBLE,
    CONSTRAINT pk_sensor PRIMARY KEY (id),
    CONSTRAINT fk_sensor_fluid FOREIGN KEY (fluid_id)
        REFERENCES fluid(id),
    CONSTRAINT fk_sensor_building FOREIGN KEY (building_id)
        REFERENCES building(id)
) ENGINE=InnoDB;

CREATE TABLE value (
    id INT AUTO_INCREMENT,
    value DOUBLE,
    date_time DATETIME,
    sensor_id INT,
    CONSTRAINT pk_value PRIMARY KEY (id),
    CONSTRAINT fk_value_sensor FOREIGN KEY (sensor_id)
        REFERENCES sensor(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
