package mobile

import "fmt"

func SayHello() {
	fmt.Println("Hello Mobile")
}

func SayHelloWithParams(name string) {
	fmt.Println("Hello", name)
}

func SayHelloWithParamsAndReturn(name string) string {
	return "Hello" + name
}

func SayHelloWithParamsAndReturnAndException(name string) (string, error) {
	return "Hello" + name, fmt.Errorf("some error")
}
