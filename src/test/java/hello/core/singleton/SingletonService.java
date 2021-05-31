package hello.core.singleton;

public class SingletonService {

    // static 영역에 단 한개만 만들어져서 올라가게 된다.
    private static final SingletonService instance = new SingletonService(); // 자기 자신을 private으로

    public static SingletonService getInstance() {
        return instance; // 자기 자신 객체 인스턴스를 단 하나만 생성해서 이를 public으로 리턴해주면 그만.
    }

    private SingletonService() { // 외부에서 SingleTon 생성자를 호출할 수 없도록 new 키워드로 객체 인스턴스를 만들 수 없어야 함

    }
}
