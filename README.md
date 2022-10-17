# treasure-conflix
English Patch for Treasure Conflix

# Memory Mapping


Offset | Content | Comment
--- | --- | ---
095B7-0AC5F | Texts for:<br/>- introduction<br/>-continue screen<br/>- save screen<br/>- ship or item names?  | Decompressed to 7E4800<br/>Encoded in SJIS
14DB0-14E6F<br/>Dialog box | ![14DB0](sprites/memory-mapping/14DB0.png) | Tiles
14E70-14F6F<br/>Info box | ![14E70](sprites/memory-mapping/14E70.png) | Tiles
18000-1802F<br/> | 19000	19EBA	1AD1D	1B832	1C6A8	1D5D9	1B832	1C6A8	1DFF9	1DFF9	1DFF9	1DFF9	1DFF9	1DFF9	1DFF9	19EBA	 | 16 Pointers
18100-1812F<br/> | 20000	2079D	20D69	2154F	21999	21E64	2154F	21999	22693	22693	22693	22693	22693	22693	22693	2079D	 | 16 Pointers
18200-1822F<br/> | 1E29C	1E6D9	1EA16	1EC69	1EEAD	1EF9A	1EC69	1EEAD	1F227	1F227	1F227	1F227	1F227	1F227	1F227	1E6D9	 | 16 Pointers
18300-1832F<br/> | 1F2BB	1F37A	1F470	1F59B	1F6C1	1F75F	1F59B	1F6C1	1F8A0	1F8A0	1F8A0	1F8A0	1F8A0	1F8A0	1F8A0	1F37A	 | 16 Pointers
18400-1842F<br/> | 2316C	26C83	278CE	2862C	2917A	2938E	2862C	2917A	29D25	29D25	29D25	29D25	29D25	29D25	29D25	26C83	 | 16 Pointers
18500-1852F<br/> | 22A39	22BC2	22C4F	22D07	22D9E	22F86	22D07	22E92	23086	23086	23086	23086	23086	23086	23086	22BC2	 | 16 Pointers
18600-1862F<br/> | 30000	40000	44197	30003	34F55	39F3C	4B421	4B421	4B421	4B421	4B421	4B421	4B421	4B421	4B421	40000	 | 16 Pointers
18700-1872F<br/> | 3E600	3E603	3E706	3E7FC	3E8DC	3E9FB	3EB98	3EACB	3EACB	3EACB	3EACB	3EACB	3EACB	3EACB	3EACB	3E603	 | 16 Pointers
1F2BB-1F379<br/>Sky Sprite | ![1F2BB](sprites/memory-mapping/1F2BB.png) | Compressed<br/>Tiles
50000-5001A | 60000	633CF	6678E	68B23	6A6B5	6C4E5	50300	51D9C	54608 | 9 pointers
50100-50123 | 56148	5651F	56981	56DA1	57078	5746D	5789F	57CD7	6E300	6E770	6EC15	6F081 | 12 pointers
50200-50220 | 6F53C	6F607	6F706	6F7FB	6F8CE	6F9C8	6F9FF	6FAE2	6FBCB	6FC78	6FD50 | 11 pointers
51D9C-54607<br/>Room 3 | ![51D9C](sprites/memory-mapping/51D9C.png) | Compressed<br/>Tiles
54608-56147<br/>Room 2 | ![54608](sprites/memory-mapping/54608.png) | Compressed<br/>Tiles
56148-5651E<br/>Room 1 | ![56148](sprites/memory-mapping/56148.png) | Compressed<br/>Tile map<br/>for tiles 68B23
5651F-56980<br/>Room 4 | ![5651F](sprites/memory-mapping/5651F.png) | Compressed<br/>Tile map<br/>for tiles 60000
5789F-57CD6<br/>Room 3 | ![5789F](sprites/memory-mapping/5789F.png) | Compressed<br/>Tile map<br/>for tiles 51D9C
60000-633CE<br/>Room 4 | ![60000](sprites/memory-mapping/60000.png) | Compressed<br/>Tiles
68B23-6A6B4<br/>Room 1 | ![68B23](sprites/memory-mapping/68B23.png) | Compressed<br/>Tiles
6E770-6EC14<br/>Room 2 | ![6E770](sprites/memory-mapping/6E770.png) | Compressed<br/>Tile map<br/>for tiles 54608
70100-76878<br/>Character sprites  | ![70100](sprites/memory-mapping/70100.png) | Compressed<br/>Tiles
76A00-7A569<br/>Sprites  | ![76A00](sprites/memory-mapping/76A00.png) | Compressed<br/>Tiles
7A600-7B0FD<br/>Texts  | ![7A600](sprites/memory-mapping/7A600.png) | Compressed<br/>Tiles<br/>4bpp (Width: 8)
7B200-7B796 | ? | ? 
7B797-7B80A | ? | ?
7B80B-7B894 | ? | ?
7B895-7C030 | ? | ?
7C8F5-7CCB7 | ? | Compressed<br/>Tile map
7CCB8-7CF63 | ? | Compressed<br/>Tile map
7CF64-7D25A | ? | Compressed<br/>Tile map
7D25B-7D30B | ? | Compressed<br/>Tile map ?
7D30C-7D657 | ? | Compressed<br/>Tile map ?
7D658-7DA86 | ? | Compressed<br/>Tile map ?
7DA87-7DB1E | ? | Compressed<br/>Tile map ?
7E200-7FFFF | ? | ?
A03A8-A03EE<br/> | ![A03A8](sprites/memory-mapping/A03A8.png) | Compressed<br/>
A1DFD-A5706<br/>Title screen | ![A1DFD](sprites/memory-mapping/A1DFD.png) | Compressed<br/>Tiles
A5720-A5A39<br/>Title screen | ![A5720](sprites/memory-mapping/A5720.png) | Compressed<br/>Tile map<br/>for tiles A1DFD
A5A3A-A5D02<br/>Title screen | ![A5A3A](sprites/memory-mapping/A5A3A.png) | Compressed<br/>Tile map<br/>for tiles A1DFD
A5D03-A5DD9<br/>Title screen | ![A5D03](sprites/memory-mapping/A5D03.png) | Compressed<br/>Tile map<br/>for tiles A1DFD
AAA78-AE4C3<br/>Continue screen | ![AAA78](sprites/memory-mapping/AAA78.png) | Compressed<br/>Tiles
AE4C4-AEA4F<br/>Continue screen | ![AE4C4](sprites/memory-mapping/AE4C4.png) | Compressed<br/>Tile map<br/>for tiles AAA78
B0000-B05CA<br/>Ship Sprite | ![B0000](sprites/memory-mapping/B0000.png) | Compressed<br/>Tiles
B06AB-B0FCF<br/>Mode 7 texts sprite | ![B06AB](sprites/memory-mapping/B06AB.png) | Compressed<br/>Tiles
B173E-B18C4<br/>Text sprites | ![B173E](sprites/memory-mapping/B173E.png) | Compressed<br/>Tiles
B19C7-B6C44<br/>Intro screen | ![B19C7](sprites/memory-mapping/B19C7.png) | Compressed<br/>Tiles
B6C45-B6ED9<br/>Intro screen | ![B6C45](sprites/memory-mapping/B6C45.png) | Compressed<br/>Tile map<br/>for tiles B19C7
B6ED8-B7151<br/>Intro screen | ![B6ED8](sprites/memory-mapping/B6ED8.png) | Compressed<br/>Tile map<br/>for tiles B19C7
B7150-B74A7<br/>Intro screen | ![B7150](sprites/memory-mapping/B7150.png) | Compressed<br/>Tile map<br/>for tiles B19C7
F6644-F82B7<br/>Intro screen | ![F6644](sprites/memory-mapping/F6644.png) | Compressed<br/>Tiles
F82B8-F86A5<br/>Intro screen | ![F82B8](sprites/memory-mapping/F82B8.png) | Compressed<br/>Tile map<br/>for tiles F6644

# Font Mapping

The game uses the font provided in the BS-X bios.<br/>
This font is mostly mapped on the Shift-JIS standard where every character is coded by two bytes.<br/>
Not part of the SJIS:
* 8-pixel wide latin characters.
* 8-pixel wide katakanas (commonly known as [half-width kanas](https://en.wikipedia.org/wiki/Half-width_kana)).

[Shift-JIS Complete Table](http://www.rikai.com/library/kanjitables/kanji_codes.sjis.shtml)


Offset | Content | Comment
--- | --- | ---
48000-50000 | ![48000](sprites/bios-mapping/01.png) | Kanjis:<br/>88 9F<br/>89 40<br/>8A 40<br/>8B 40 - 8B 5B
50000-58000 | ![50000](sprites/bios-mapping/02.png) | Kanjis:<br/>8B 5C - 8B xx<br/>8C 40<br/>8D 40<br/>8E 40<br/>8F 40<br/>90 40 - 90 B0<br/>
58000-60000 | ![58000](sprites/bios-mapping/03.png) | Kanjis:<br/>90 B1 - 90 xx<br/>91 40<br/>92 40<br/>93 40<br/>94 40<br/>95 40
60000-68000 | ![60000](sprites/bios-mapping/04.png) | Kanjis:<br/>96 40<br/>97 40<br/>98 40<br/>99 40<br/>9A 40<br/>9B 40
68000-70000 | ![68000](sprites/bios-mapping/05.png) | Kanjis:<br/>9C 40<br/>9D 40<br/>9E 40<br/>9F 40<br/>E0 40
70000-78000 | ![70000](sprites/bios-mapping/06.png) | Kanjis:<br/>E1 40<br/>E2 40<br/>E3 40<br/>E4 40<br/>E5 40<br/>E6 40
78000-80000 | ![78000](sprites/bios-mapping/07.png) | Kanjis:<br/>E7 40<br/>E8 40<br/>E9 40<br/>EA 40
