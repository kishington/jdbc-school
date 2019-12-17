CREATE TABLE IF NOT EXISTS public.groups
(
    group_id integer,
    group_name character varying,
    PRIMARY KEY (group_id)
);

ALTER TABLE public.groups
    OWNER to "Sasha";
    