from pathlib import Path

path = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java/vazkii/psi/common/item/ItemCAD.java")
text = path.read_text(encoding="utf-8")
start = text.index("\t@Override\n\tpublic void verifyComponentsAfterLoad(ItemStack pStack) {")
body_start = text.index("{", start)
depth = 0
end = None
for index in range(body_start, len(text)):
    if text[index] == "{":
        depth += 1
    elif text[index] == "}":
        depth -= 1
        if depth == 0:
            end = index + 1
            break

replacement = "\tpublic void verifyComponentsAfterLoad(ItemStack pStack) {\n\t\t// Legacy pre-component CAD data is intentionally not migrated in the interim 1.21.11 runtime.\n\t}\n"
path.write_text(text[:start] + replacement + text[end:], encoding="utf-8")
print("Replaced legacy ItemCAD CustomData migration with compatibility no-op")
