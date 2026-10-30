import anyio
from mcp import Client


async def main() -> int:
    async with Client("http://localhost:8080/mcp") as client:
        tools = await client.list_tools()
        print("Available tools:")
        for tool in tools.tools:
            print(f"- {tool.name}")

        result = await client.call_tool(
            "match_name_simple",
            {"kanji": "山田　太郎", "kana": "ヤマダ　タロウ"},
        )

        print("\nmatch_name_simple result:")
        print(result.structured_content or result.content)

        if result.is_error:
            print("Tool call failed.")
            return 1

    return 0


if __name__ == "__main__":
    raise SystemExit(anyio.run(main))