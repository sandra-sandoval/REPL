export async function example_function(args: Array<string>): Promise<string> {
  const userName = args[0];
  const message = "hello " + userName;
  return message;
}
