
# 專題名稱：星際大戰 (Star Wars Ultimate)

## 👥 小組資訊

* **組別名稱**：GXX_kevinzhong0318_47_leepetercat_0975365318
* **小組 public GitHub repo**：[點此連結](https://github.com/kevinzhong0318/GXX_kevinzhong0318_47_-leepetercat-09)
* **備註**：本組後續將使用 Pull Request 紀錄每週進度。

### 組員名單

| 姓名             | 學號      | GitHub                                         | 角色分工                     |
| :--------------- | :-------- | :--------------------------------------------- | :--------------------------- |
| **鐘善民** | B11107147 | [kevinzhong318](https://github.com/kevinzhong318) | 核心邏輯、敵人 AI、關卡設計  |
| **李品學** | B11332009 | [leepetercat](https://github.com/leepetercat)     | 介面開發、資源管理、專案控管 |

---

## 🎮 遊玩方式說明

本遊戲支援跨平台操作，請確保您的輸入法處於英文模式以獲得最佳體驗。

* **滑鼠操作**：
  * `左鍵`：射擊單發子彈。
  * `右鍵`：同時射擊兩發擴散子彈。
* **鍵盤快捷鍵**：
  * `A`：開啟或關閉 **無敵模式**（God Mode）。
  * `Esc`：**暫停遊戲**（可進一步選擇繼續或回到主選單）。
  * `L`：暫停選單中，選擇「繼續遊戲」。
  * `M`：暫停選單中，選擇「回到主選單」。

---

## 📊 遊戲流程圖

我們使用 Mermaid 語法來展示遊戲的狀態切換與核心邏輯，確保程式架構清晰。

```mermaid
graph TD
    %% 啟動與主選單
    Start((遊戲啟動)) --> Init[初始化視窗與頁面]
    Init --> Menu{主選單}
  
    Menu -- 選擇關卡 --> Game[進入遊戲面板]
    Menu -- 點擊退出 --> Exit([關閉程式])
  
    %% 遊戲核心邏輯
    subgraph Gaming[遊戲進行中]
        Game --> Loop[遊戲迴圈 Timer]
        Loop --> Input{偵測輸入}
    
        %% 暫停分支
        Input -- Esc --> Pause{暫停狀態?}
        Pause -- 是 --> P_Menu[暫停遮罩顯示]
        P_Menu -- L 鍵 --> Game
        P_Menu -- M 鍵 --> Menu
    
        %% 戰鬥分支
        Pause -- 否 --> Update[物理更新與碰撞判定]
        Update -- 無敵模式 A --> God[鎖定 HP 不減]
        Update --> Result{判定勝負}
    end
  
    %% 結算
    Result -- 玩家 HP <= 0 --> Fail[任務失敗畫面]
    Result -- 敵人全滅 --> Win[關卡完成畫面]
  
    Fail -- 點擊滑鼠 --> Menu
    Win -- 點擊滑鼠 --> Menu
```
