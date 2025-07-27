USE TESTDB;
GO

-- Step 1: Insert 50 users (user1 to user50)
DECLARE @i INT = 1;
WHILE @i <= 50
BEGIN
    INSERT INTO dbo.users (id, username, password, country_code)
    VALUES (NEWID(), CONCAT('user', @i), 'password123', 'MY');
    SET @i += 1;
END

-- Step 2: Store all user IDs in a temporary table for processing follows
IF OBJECT_ID('tempdb..#UserList') IS NOT NULL DROP TABLE #UserList;
SELECT id, username INTO #UserList FROM dbo.users;

-- Step 3: Insert follows (everyone follows everyone else except self)
DECLARE @follower_id UNIQUEIDENTIFIER, @followee_id UNIQUEIDENTIFIER;

DECLARE follower_cursor CURSOR FOR SELECT id FROM #UserList;
OPEN follower_cursor;
FETCH NEXT FROM follower_cursor INTO @follower_id;

WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE followee_cursor CURSOR FOR SELECT id FROM #UserList WHERE id != @follower_id;
    OPEN followee_cursor;
    FETCH NEXT FROM followee_cursor INTO @followee_id;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        INSERT INTO dbo.follows (follower_id, followee_id)
        VALUES (@follower_id, @followee_id);

        FETCH NEXT FROM followee_cursor INTO @followee_id;
    END

    CLOSE followee_cursor;
    DEALLOCATE followee_cursor;

    FETCH NEXT FROM follower_cursor INTO @follower_id;
END

CLOSE follower_cursor;
DEALLOCATE follower_cursor;

DROP TABLE #UserList;

DECLARE @user1_id UNIQUEIDENTIFIER;
SELECT @user1_id = id FROM dbo.users WHERE username = 'user1';

INSERT INTO dbo.places (place_id, user_id, name, category, address)
VALUES 
('ChIJLf8zWebR5zsRkVxdjd6rbKI', @user1_id, 'Pizza By The Bay', 'Italian Restaurant', 'Soona Mahal, 143, Marine Dr, Churchgate, Mumbai, Maharashtra 400020, India'),
('ChIJ2ZVFCzIXrjsRU64njYB04RE', @user1_id, 'Pizza 4P''s Indiranagar', 'Italian Restaurant', 'No.3275/A, 12th Main Rd, HAL 2nd Stage, Ward 72, Domlur, Bengaluru, Karnataka 560038, India'),
('ChIJW3BnIA6pQjQRR5BYohped7A', @user1_id, 'Pizza power', 'Pizza Restaurant', '235, Taiwan, New Taipei City, Zhonghe District, Shuiyuan Rd, 102號光華街口萊爾富對面'),
('ChIJC-nWiTYvdTERGMJvRSoZ2f4', @user1_id, 'Pizza 4P''s Hai Ba Trung', 'Pizza Restaurant', '151b Hai Bà Trưng, Phường 6, Quận 3, Hồ Chí Minh 700000, Vietnam'),
('ChIJq4DSNt0_K4gR5BFOlAqpcb8', @user1_id, 'Pizza CraviN (Brampton)', 'Pizza Restaurant', '7910 Hurontario St #18, Brampton, ON L6Y 0P6, Canada'),
('ChIJsxSZsJDS3TsRXCEonV2EB8s', @user1_id, 'Nashik', NULL, 'Nashik, Maharashtra, India'),
('ChIJi9nuRgiYHkcR421AlKwpIfA', @user1_id, 'Nasielsk', NULL, '05-190 Nasielsk, Poland'),
('ChIJq52-7AJZaTkR96wMc6qjAV0', @user1_id, 'Nasirabad', NULL, 'Nasirabad, Rajasthan 305601, India'),
('ChIJWfwHc0AMXUcR4MorhlCtAAQ', @user1_id, 'Našice', NULL, '31500, Našice, Croatia'),
('ChIJyT_j-q_q3TsRpuqorfro2ho', @user1_id, 'Nashik Road', NULL, 'Nashik Road, Nashik, Maharashtra, India'),
('ChIJkVOvMdpJzDERIIpbDIqX7gY', @user1_id, 'Stadium Merdeka', 'Stadium', 'Jalan Stadium, Presint Merdeka 118, 50118 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia'),
('ChIJRd6Qa5ZKzDERpnaQSpbCpDk', @user1_id, 'National Stadium Bukit Jalil', 'Stadium', 'Jalan Barat, Bukit Jalil, 57000 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia'),
('ChIJt__YxmYZ2jERKih8lynJnUY', @user1_id, 'Stadium MRT Station (CC6)', NULL, '3 Stadium Walk, Singapore 397692'),
('ChIJd0b0EWvxaS4R8Hb9SpKttuA', @user1_id, 'Komplek Gelora Bung Karno', 'Sports Complex', 'Jl. Pintu Satu Senayan, Gelora, Kecamatan Tanah Abang, Kota Jakarta Pusat, Daerah Khusus Ibukota Jakarta 10270, Indonesia'),
('ChIJm4WvuE7jAGARbdCr0f3bdcE', @user1_id, 'Panasonic Stadium Suita', 'Stadium', '3-3 Senribanpakukōen, Suita, Osaka 565-0826, Japan'),
('ChIJ0-cIvSo2zDERmWzYQPUfLiM', @user1_id, 'Federal Territory of Kuala Lumpur', NULL, 'Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia'),
('ChIJL4Zul54JtzERkiQjy4XuwBs', @user1_id, 'Kuala Terengganu', NULL, 'Kuala Terengganu, Terengganu, Malaysia'),
('ChIJ5-rvAcdJzDERfSgcL1uO2fQ', @user1_id, 'Kuala Lumpur', NULL, 'Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia'),
('ChIJ1WJ2bOi8yjERxbofruAstSQ', @user1_id, 'Kuala Kangsar', NULL, 'Kuala Kangsar, Perak, Malaysia'),
('ChIJL4PyKDOLzDERHYHAzC9YBdw', @user1_id, 'Kuala Selangor', NULL, 'Kuala Selangor, Selangor, Malaysia');

