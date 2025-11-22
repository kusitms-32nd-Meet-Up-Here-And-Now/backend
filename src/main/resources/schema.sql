CREATE INDEX IF NOT EXISTS idx_place_location_gist
    ON place
        USING GIST (location);