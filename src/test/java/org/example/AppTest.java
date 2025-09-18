package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AppTest {

    public static String run(String input) {
        ByteArrayOutputStream output = TestUtil.setOutToByteArray();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
            IdiomController controller = new IdiomController(in);

            controller.start(); // 루프가 정상적으로 종료되도록 "종료" 명령 포함

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            TestUtil.clearSetOutToByteArray(output);
        }
    }

    public static void clear() {
        // 현재 구현에서는 Repository나 Service 내 데이터 초기화 코드가 필요함
        // 예: IdiomRepository.clearAll() 같은 메서드 추가
    }
}
